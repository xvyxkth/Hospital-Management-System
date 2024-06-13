import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { UserProvider } from './pages/UserContext';
import Pharmacy from './pages/Pharmacy';
import Login from './pages/Login';
import Patient from './pages/Patient';
import Appointment from './pages/Appointment';
import Doctor from './pages/Doctor';
import Ward from './pages/Ward';
import Admin from './pages/Admin';
import DocDB from './pages/DocDB';
import Payments from './pages/Payments';
import Employee from './pages/Employee';
import AppointmentList from './pages/AppointmentList';
import AppointmentListForDoctor from './pages/AppointmentListForDoctor';

function App() {
    return (
        <UserProvider>
            <div>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<Login />} />
                        <Route path="/Pharmacy" element={<Pharmacy />} />
                        <Route path="/Patient" element={<Patient />} />
                        <Route path="/Appointment" element={<Appointment />} />
                        <Route path="/Doctor" element={<Doctor />} />
                        <Route path="/Ward" element={<Ward />} />
                        <Route path="/Admin" element={<Admin />} />
                        <Route path="/DocDB" element={<DocDB />} />
                        <Route path="/Payments" element={<Payments />} />
                        <Route path="/Employee" element={<Employee />} />
                        <Route path="/AppointmentList" element={<AppointmentList />} />
                        <Route path="/AppointmentListForDoctor" element={<AppointmentListForDoctor />} />
                    </Routes>
                </BrowserRouter>
            </div>
        </UserProvider>
    );
}

export default App;
