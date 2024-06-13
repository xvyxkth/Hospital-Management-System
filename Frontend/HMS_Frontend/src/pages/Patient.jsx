import React, { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import FeedbackBox from './FeedbackBox';
import UserContext from './UserContext';

function Patient() {
    const { userString } = useContext(UserContext);
    const navigate = useNavigate();

    const SignOut = () => {
        navigate("/");
    };
    const toPharmacy = () => {
        navigate("/Pharmacy");
    };
    const toAppointments = () => {
        navigate("/Appointment");
    };
    return (
        <div>
            <h1>Hello {userString}!</h1>
            <button onClick={SignOut}>Sign Out</button><br/><br/>
            <button onClick={toPharmacy}>Visit Our Store</button><br/><br/>
            <button onClick={toAppointments}>Book or Modify An Appointment</button><br/><br/>
            <FeedbackBox/>
        </div>
    );
}

export default Patient;
