import React, { useState, useContext, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import UserContext from './UserContext';
import axios from "axios"

function Doctor() {
    const { userString } = useContext(UserContext);
    const [doctor, setDoctor] = useState("");

    useEffect(() => {
        const fetchDoctor = async () => {
            try {
                const res = await axios.get("http://localhost:8800/doctorForDoctorPage", {
                    params: { doctorID: userString }
                });
                setDoctor(res.data);
            } catch (err) {
                console.log("Error in Fetch");
                console.log(err);
            }
        };
        fetchDoctor();
    }, [userString]);

    const navigate = useNavigate();
    const backToLogin = () => {
        navigate("/");
    };
    const toWards = () => {
        navigate("/Ward");
    };

    const displayAllAppointments = () => {
        navigate('/AppointmentListForDoctor')
    };

    return (
        <div>
            <h1>Hello { doctor }!</h1>
            <button onClick={backToLogin}>Sign Out</button><br/><br/>
            <button onClick = {displayAllAppointments}>View All Appointments</button><br/><br/>
            <button onClick={toWards}>Available Wards</button><br/><br/>
        </div>
    );
}

export default Doctor;
