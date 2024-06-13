import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";
import UserContext from './UserContext';

const Login = () => {
    const [pwdString, setPwdString] = useState("");
    const { userString, setUserString } = useContext(UserContext);
    const navigate = useNavigate();

    const handlePwdInput = (event) => {
        setPwdString(event.target.value);
    };

    const SignIn = (event) => {
        event.preventDefault();
        axios.post("http://localhost:8800/login", { username: userString, password: pwdString })
            .then(res => {
                if (res.data[0].userID[0] === 'a') {
                    navigate('/Admin');
                }
                else if (res.data[0].userID[0] === 'p') {
                    setUserString(userString);
                    navigate('/Patient');
                }
                else if (res.data[0].userID[0] === 'd' || res.data[0].userID[0] === 'r') {
                    navigate('/Doctor');
                }
                else {
                    alert("Invalid Credentials");
                }
            })
            .catch(err => {
                console.log(err);
                alert("An error occurred. Please try again.");
            });
    };

    const SignUp = (event) => {
        event.preventDefault();
        axios.post("http://localhost:8800/signup", { username: userString, password: pwdString })
            .then(res => {
                if (res.data.success) {
                    if (userString[0] === 'a') {
                        navigate('/Admin');
                    } else if (userString[0] === 'p') {
                        navigate('/Patient');
                    } else if (userString[0] === 'd' || userString[0] === 'r') {
                        navigate('/Doctor');
                    }
                } else {
                    alert(res.data.message);
                }
            })
            .catch(err => {
                console.log(err);
                alert("An error occurred. Please try again.");
            });
    };

    return (
        <div>
            <h1>Pilani General Hospital</h1>
            <h3>Healthcare for Good</h3>
            <h3>Today. Tomorrow. Always.</h3>
            Username: <input type="text" placeholder="Enter Your Username" value={userString} onChange={(e) => setUserString(e.target.value)}></input><br /><br />
            Password: <input type="password" placeholder="Enter Your Password" value={pwdString} onChange={handlePwdInput}></input><br /><br />
            <button onClick={SignIn}> Sign In </button><br /><br />
            <button onClick={SignUp}> Sign Up </button>
        </div>
    );
}

export default Login;
