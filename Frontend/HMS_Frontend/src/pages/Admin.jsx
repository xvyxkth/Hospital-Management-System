import React from 'react'
import { useNavigate } from 'react-router-dom'

function Admin(){
    const navigate = useNavigate()
    const backToLogin = () => {
        navigate("/")
    }
    const toEmployee = () => {
        navigate("/Employee")
    }
    const toAppointmentList = () => {
        navigate("/AppointmentList")
    }
    const toPayments = () => {
        navigate("/Payments")
    }
    const toWards = () => {
        navigate("/Ward")
    }
    const toDocDB = () => {
        navigate("/DocDB")
    }
    return(
        <div>
            <h1>This is the Admin Page</h1>
            <button onClick = {backToLogin}>Sign Out</button><br/><br/>
            <button onClick = {toEmployee}>Manage Staffing</button><br/><br/>
            <button onClick = {toAppointmentList}>Upcoming Appointments</button><br/><br/>
            <button onClick = {toPayments}>Billing</button><br/><br/>
            <button onClick = {toWards}>Available Wards</button><br/><br/>
            <button onClick = {toDocDB}>Staff Records</button><br/><br/>
        </div>
    )
}

export default Admin