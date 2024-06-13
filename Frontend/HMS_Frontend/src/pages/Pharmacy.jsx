import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

function Pharmacy() {
    const [balance, setBalance] = useState(0);
    const navigate = useNavigate();
    const [medicines, setMedicines] = useState([]);
    const [quantities, setQuantities] = useState({});
    const [latestPaymentID, setLatestPaymentID] = useState(0);

    useEffect(() => {
        const fetchAllMedicines = async () => {
            try {
                const res = await axios.get("http://localhost:8800/pharmacy");
                setMedicines(res.data);
                const initialQuantities = {};
                res.data.forEach(medicine => {
                    initialQuantities[medicine.medID] = 0;
                });
                setQuantities(initialQuantities);
            } catch (err) {
                console.log("Error in Fetch");
                console.log(err);
            }
        };
        fetchAllMedicines();

        const fetchLatestPaymentID = async () => {
            try {
                const res = await axios.get("http://localhost:8800/latestPaymentID");
                if (res.data.length > 0) {
                    setLatestPaymentID(res.data[0].paymentID);
                } else {
                    setLatestPaymentID(0);
                }
            } catch (err) {
                console.log("Error in Fetching Latest Payment ID");
                console.log(err);
            }
        };
        fetchLatestPaymentID();
    }, []);

    const goBack = () => {
        history.back();
    };

    const addButton = (medicine) => {
        setBalance(balance + medicine.price);
        setQuantities({
            ...quantities,
            [medicine.medID]: quantities[medicine.medID] + 1
        });
    };

    const minusButton = (medicine) => {
        if (quantities[medicine.medID] > 0) {
            setBalance(balance - medicine.price);
            setQuantities({
                ...quantities,
                [medicine.medID]: quantities[medicine.medID] - 1
            });
        }
    };

    const buyButton = async (event) => {
        event.preventDefault();
        try {
            const newPaymentID = latestPaymentID + 1;
            await axios.post('http://localhost:8800/paymentrecord', {
                paymentID: newPaymentID,
                amount: balance
            });
            setLatestPaymentID(newPaymentID);
            setBalance(0);
            const initialQuantities = {};
            medicines.forEach(medicine => {
                initialQuantities[medicine.medID] = 0;
            });
            setQuantities(initialQuantities);
            alert("Purchase Successful");
        } catch (error) {
            console.log(error);
            alert("An error occurred. Please try again.");
        }
    };

    return (
        <div>
            <h1>This is the Pharmacy Page</h1>
            <h2>Your Total is : {balance} Rs.</h2>
            <div className="Medicines">
                {medicines.map(medicine => (
                    <div className="Medicine" key={medicine.medID}>
                        <h3>{medicine.medName}</h3>
                        <p>Price : {medicine.price} Rs. / Tablet</p>
                        <button onClick={() => addButton(medicine)}>+</button>&nbsp;&nbsp;
                        <button onClick={() => minusButton(medicine)}>-</button><br /><br />
                        Quantity : &nbsp;
                        <input
                            type="text"
                            readOnly
                            value={quantities[medicine.medID]}
                            style={{ width: '30px', textAlign: 'center' }}
                        />
                        <br /><br />
                    </div>
                ))}
            </div><br />
            <button onClick={goBack}>Back</button><br /><br />
            <button onClick={buyButton}>Buy</button>
        </div>
    );
}

export default Pharmacy;