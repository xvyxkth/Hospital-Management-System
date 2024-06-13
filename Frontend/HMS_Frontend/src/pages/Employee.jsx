import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

function Employee() {
    const navigate = useNavigate();
    const [employee, setEmployee] = useState({
        name: '',
        age: '',
        employeeID: '',
        salary: '',
        email: '',
        designation: '',
        specialisation: ''
    });
    const [deleteID, setDeleteID] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEmployee(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const AddEmployee = () => {
        axios.post("http://localhost:8800/addEmployee", employee)
            .then(res => {
                alert("Employee added successfully!");
                setEmployee({
                    name: '',
                    age: '',
                    employeeID: '',
                    salary: '',
                    email: '',
                    designation: '',
                    specialisation: ''
                });
            })
            .catch(err => {
                console.log(err);
                alert("An error occurred. Please try again.");
            });
    };

    const DeleteEmployee = async () => {
        try {
            const res = await axios.get('http://localhost:8800/fetchWardsOfDoctor', {
                params: { employeeID: deleteID }
            });
            const wardsOfDoctor = res.data;
            await axios.post("http://localhost:8800/deleteEmployee", { wards: wardsOfDoctor, employeeID: deleteID });
            alert("Employee deleted successfully!");
            setDeleteID('');
        } catch (err) {
            console.log(err);
            alert("An error occurred. Please try again.");
        }
    };

    const backToAdmin = () => {
        navigate("/Admin");
    };

    return (
        <div>
            This is the Employee Page<br /><br />
            <button onClick={backToAdmin}>Back</button><br /><br />
            <div>
                <input type="text" name="name" placeholder="Name" value={employee.name} onChange={handleChange} /><br /><br />
                <input type="text" name="age" placeholder="Age" value={employee.age} onChange={handleChange} /><br /><br />
                <input type="text" name="employeeID" placeholder="Employee ID" value={employee.employeeID} onChange={handleChange} /><br /><br />
                <input type="text" name="salary" placeholder="Salary" value={employee.salary} onChange={handleChange} /><br /><br />
                <input type="text" name="email" placeholder="Email" value={employee.email} onChange={handleChange} /><br /><br />
                <input type="text" name="designation" placeholder="Designation" value={employee.designation} onChange={handleChange} /><br /><br />
                {employee.designation.toLowerCase() === 'doctor' && (
                    <div>
                        <input type="text" name="specialisation" placeholder="Specialisation" value={employee.specialisation} onChange={handleChange} /><br /><br />
                    </div>
                )}
                <button onClick={AddEmployee}>Add</button><br /><br />
            </div>
            <div>
                <input type="text" placeholder="Employee ID to Delete" value={deleteID} onChange={(e) => setDeleteID(e.target.value)} /><br /><br />
                <button onClick={DeleteEmployee}>Delete</button><br /><br />
            </div>
        </div>
    );
}

export default Employee;
