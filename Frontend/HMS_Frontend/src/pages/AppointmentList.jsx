import React, { useState, useEffect, useContext } from 'react'
import { useNavigate } from 'react-router-dom'
import UserContext from "./UserContext"
import axios from "axios"

function AppointmentList(){
    const navigate = useNavigate()

    const [appointments, setAllAppointments] = useState([])

    useEffect(() => {
        const fetchAppointments = async () => {
            try{
                const res = await axios.get("http://localhost:8800/appointmentList")
                setAllAppointments(res.data)
            }catch(err){
                console.log("Failed to Fetch Appointments")
                console.log(err)
            }
        }
        fetchAppointments()
    }, [])


    const backToAdmin = () => {
        navigate('/Admin')
    }
    return(
        <div>
            <h1>Appointment List</h1>
            <div className="appointmentTable">
                <table>
                    <thead>
                        <tr>
                            <th>Appointment ID</th>
                            <th>Doctor ID</th>
                            <th>Patient ID</th>
                            <th>Appointment Date</th>
                            <th>Ward ID</th>
                            <th>Slot</th>
                        </tr>
                    </thead>
                    <tbody>
                        {appointments.map(app => (
                            <tr key={app.appID}>
                                <td>{app.appID}</td>
                                <td>{app.doctorID}</td>
                                <td>{app.patientID}</td>
                                <td>{app.appDate}</td>
                                <td>{app.wardID}</td>
                                <td>{app.slot}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button onClick = {backToAdmin}>Back</button><br/><br/>
        </div>
    )
}

export default AppointmentList