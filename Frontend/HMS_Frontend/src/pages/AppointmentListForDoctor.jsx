import React, {useState, useEffect, useContext} from 'react'
import { useNavigate } from 'react-router-dom'
import UserContext from './UserContext'
import axios from "axios"

function AppointmentListForDoctor(){
    const navigate = useNavigate()

    const {userString} = useContext(UserContext)
    console.log(userString)

    const [appointments, setAllAppointments] = useState([])

    useEffect(() => {
        const fetchAppointments = async () => {
            try{
                const res = await axios.get("http://localhost:8800/appointmentListForDoctor", {
                    params: {
                        docID : userString
                    }
                })
                setAllAppointments(res.data)
            }catch(err){
                console.log("Failed to Fetch Appointments")
                console.log(err)
            }
        }
        fetchAppointments()
    }, [])


    const backToDoctor = () => {
        navigate('/Doctor')
    }
    return(
        <div>
            <h1>These are your appointments</h1>
            <div className="appointmentTableForDoctor">
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
            <button onClick = {backToDoctor}>Back</button><br/><br/>
        </div>
    )
}

export default AppointmentListForDoctor