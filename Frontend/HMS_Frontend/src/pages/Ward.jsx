import React, {useState, useEffect} from 'react'
import axios from "axios"

function Ward(){

    const [wards, setWards] = useState([])

    useEffect(() => {
        const fetchAvlWards = async () => {
            try{
                const res = await axios.get("http://localhost:8800/ward")
                setWards(res.data)
    
            }catch(err){
                console.log("Error in Fetch")
                console.log(err)
            }
        }
        fetchAvlWards()
    }, [])

    console.log(wards)

    const backButton = () => {
        history.back()
    }
    return(
        <div>
            <h1>These are the available wards</h1><br/><br/>
            <div className="wardTable">
                <table>
                    <thead>
                        <tr>
                            <th>Ward ID</th>
                            <th>Ward Name</th>
                        </tr>
                    </thead>
                    <tbody>
                        {wards.map(ward => (
                            <tr key={ward.wardID}>
                                <td>{ward.wardID}</td>
                                <td>{ward.wardName}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <button onClick = {backButton}>Back</button><br/><br/>
        </div>
    )
}

export default Ward