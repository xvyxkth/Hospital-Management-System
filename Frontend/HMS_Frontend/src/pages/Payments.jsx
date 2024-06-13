import React, {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom'
import axios from "axios"

function Payments(){

    const [paymentRecords, setPaymentRecords] = useState([])

    useEffect(() => {
        const fetchRecords = async () => {
            try{
                const res = await axios.get("http://localhost:8800/fetchrecord")
                setPaymentRecords(res.data)
            }catch(err){
                console.log("Error in Fetch")
                console.log(err)
            }
        }
        fetchRecords()
    }, [])

    const navigate = useNavigate()
    const backToAdmin = () => {
        navigate('/Admin')
    }
    return(
        <div>
            <h1>Payment Records</h1>
            <div className="paymentsTable">
                <table>
                    <thead>
                        <tr>
                            <th>Payment ID</th>
                            <th>Payment From Patient</th>
                        </tr>
                    </thead>
                    <tbody>
                        {paymentRecords.map(pr => (
                            <tr key={pr.paymentID}>
                                <td>{pr.paymentID}</td>
                                <td>{pr.amount}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button onClick = {backToAdmin}>Back</button><br/><br/>
        </div>
    )
}

export default Payments