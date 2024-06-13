import React, { useState, useEffect, useContext} from "react";
import axios from "axios";
let feedbackNumber = 0
import UserContext from './UserContext';

function FeedbackBox(){
    const { userString } = useContext(UserContext);
    const patientID = userString
    const [feedbackString, setFeedback] = useState("");
    const [doctors, setDoctors] = useState([])
    const [feedbackDoctor, setFeedbackDoctor] = useState("");

    useEffect(() => {
        const fetchAllDoctors = async () => {
            try{
                const res = await axios.get("http://localhost:8800/doctors")
                setDoctors(res.data)
    
            }catch(err){
                console.log("Error in Fetch")
                console.log(err)
            }
        }
        fetchAllDoctors()
    }, [])

    const subFeedback = (event) => {
        console.log(feedbackString)
        event.preventDefault()
        feedbackNumber += 1;
        axios.post('http://localhost:8800/doctors', {
            feedbackNo: feedbackNumber,
            patientID: patientID,
            employeeName: feedbackDoctor,
            review: feedbackString
        }).then(response => {
            console.log(response.data);
        }).catch(error => {
            console.log(error);
        });
    };

    const handleFeedback = (event) => {
        setFeedback(event.target.value);
    };

    const selectDoctor = (event) => {
        setFeedbackDoctor(event.target.innerText);
        console.log(event.target.innerText)
    };

    return (
        <div className="feedback-box">
            <p className="feedback-heading">
                Feedback Box
            </p>
            <p>Selected Doctor : {feedbackDoctor}</p>
            <input 
                type="text" 
                placeholder="Enter Your Feedback Here"
                value={feedbackString} 
                onChange={handleFeedback}
                className="feedback-input"
            /><br/>
            
            <div className="dropdown">
                <button>Select Your Doctor</button>
                <div className="content">
                    {doctors.map(doctor =>(
                        <>
                            <a className="Doctors" key={doctor.employeeID} onClick={selectDoctor}>
                                {doctor.docName}
                            </a>
                        </>
                    ))}
                </div>
            </div><br/>
            <button onClick={subFeedback} className="feedback-btn">Submit Feedback</button>
        </div>
    );
}

export default FeedbackBox;
