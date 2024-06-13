import React, {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom'
import axios from "axios"

function DocDB(){
    const [workers, setWorkers] = useState([])
    useEffect(() => {
        const fetchWorkers = async () => {
            try{
                const res = await axios.get("http://localhost:8800/docDB")
                setWorkers(res.data)
            }
            catch(err){
                console.log("Failed to fetch the Workers")
                console.log(err)
            }
        }
        fetchWorkers()
    }, [])
    const navigate = useNavigate()
    const backToAdmin = () => {
        navigate('/Admin')
    }
    return(
        <div>
            <h1>This is the Doctor Database Page</h1>
            <div className="employeeTable">
                <table>
                    <thead>
                        <tr>
                            <th>Employee ID</th>
                            <th>Name</th>
                            <th>Age</th>
                            <th>Salary</th>
                            <th>Designation</th>
                            <th>E-Mail ID</th>
                        </tr>
                    </thead>
                    <tbody>
                        {workers.map(worker => (
                            <tr key={worker.employeeID}>
                                <td>{worker.employeeID}</td>
                                <td>{worker.name}</td>
                                <td>{worker.age}</td>
                                <td>{worker.salary}</td>
                                <td>{worker.designation}</td>
                                <td>{worker.email}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button onClick = {backToAdmin}>Back</button><br/><br/>
        </div>
    )
}

export default DocDB