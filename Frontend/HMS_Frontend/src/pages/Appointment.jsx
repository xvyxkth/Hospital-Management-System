import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import UserContext from './UserContext';

const Appointment = () => {
    const [doctors, setDoctors] = useState([]);
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [appointments, setAppointments] = useState([]);
    const [wards, setWards] = useState([]);
    const { userString } = useContext(UserContext);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const res = await axios.get('http://localhost:8800/doctors');
                setDoctors(res.data);
            } catch (err) {
                console.log(err);
            }
        };

        const fetchWards = async () => {
            try {
                const res = await axios.get('http://localhost:8800/ward');
                setWards(res.data);
            } catch (err) {
                console.log(err);
            }
        };

        fetchDoctors();
        fetchWards();
        fetchAppointments(selectedDate);
    }, [selectedDate]);

    const fetchAppointments = async (date) => {
        try {
            const res = await axios.get('http://localhost:8800/appointments', {
                params: { date: date.toISOString().split('T')[0] }
            });
            setAppointments(res.data);
        } catch (err) {
            console.log(err);
        }
    };

    const bookWard = async (wardToBook) => {
        try {
            await axios.post('http://localhost:8800/bookWard', { wardToBook });
        } catch (err) {
            console.log(err);
        }
    };

    const unbookWard = async (wardToUnbook) => {
        try {
            await axios.post('http://localhost:8800/unbookWard', { wardToUnbook });
        } catch (err) {
            console.log(err);
        }
    };

    const bookAppointment = async (doctorID, slot) => {
        const patientID = userString;
        const appDate = selectedDate.toISOString().split('T')[0];
        const wardID = wards[0].wardID;

        const existingAppointment = appointments.find(
            appt => appt.doctorID === doctorID && appt.slot === slot
        );

        if (existingAppointment) {
            alert('Please choose another time slot');
            return;
        }

        try {
            const res = await axios.post('http://localhost:8800/bookAppointment', {
                doctorID,
                patientID,
                appDate,
                wardID,
                slot
            });

            if (res.data.success) {
                fetchAppointments(selectedDate);
                await bookWard(wardID);
            } else {
                alert('Failed to book appointment');
            }
        } catch (err) {
            console.error(err);
            alert('An error occurred. Please try again.');
        }
    };

    const deleteAppointment = async (appointmentID, wardID) => {
        try {
            const res = await axios.post('http://localhost:8800/deleteAppointment', { appointmentID, wardID });

            if (res.data.success) {
                await unbookWard(wardID);
                fetchAppointments(selectedDate);
            } else {
                alert('Failed to delete appointment');
            }
        } catch (err) {
            console.error(err);
            alert('An error occurred. Please try again.');
        }
    };

    const handleDateChange = (days) => {
        const newDate = new Date(selectedDate);
        newDate.setDate(newDate.getDate() + days);
        setSelectedDate(newDate);
    };

    return (
        <div>
            <h1>Book an Appointment</h1>
            <button onClick={() => handleDateChange(-1)}>Previous Day</button>&nbsp;&nbsp;
            <button onClick={() => handleDateChange(1)}>Next Day</button>
            <h2>{selectedDate.toDateString()}</h2>
            <div>
                {doctors.map((doctor) => (
                    <div key={doctor.employeeID}>
                        <h3>{doctor.docName}</h3>
                        {[1, 2, 3, 4, 5].map((slot) => {
                            const slotTimes = ["10AM-11AM", "11AM-12PM", "4PM-5PM", "5PM-6PM", "6PM-7PM"];
                            const isBooked = appointments.some(
                                (appt) => appt.doctorID === doctor.employeeID && appt.slot === slot
                            );
                            return (
                                <pre>
                                <button
                                    key={slot}
                                    style={{ backgroundColor: isBooked ? 'red' : 'green' }}
                                    onClick={() => {
                                        if (isBooked) {
                                            alert('Please choose another time slot');
                                        } else {
                                            bookAppointment(doctor.employeeID, slot);
                                        }
                                    }}
                                >
                                    {slotTimes[slot - 1]}
                                </button>
                                </pre>
                            );
                        })}
                    </div>
                ))}
            </div>
            <h1>Your Appointments</h1>
            <div>
                {appointments.filter(appt => appt.patientID === userString).map((appt) => (
                    <div key={appt.appID}>
                        <h3>Doctor: {appt.doctorID}</h3>
                        <p>Slot: {["10AM-11AM", "11AM-12PM", "4PM-5PM", "5PM-6PM", "6PM-7PM"][appt.slot - 1]}</p>
                        <button onClick={() => deleteAppointment(appt.appID, appt.wardID)}>Delete Appointment</button>
                    </div>
                ))}
            </div><br/>
            <button onClick={() => navigate('/Patient')}>Back</button>
        </div>
    );
};

export default Appointment;
