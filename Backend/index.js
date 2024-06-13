import express from "express";
import mysql from "mysql2";
import cors from "cors";

const app = express();

const db = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "root@123",
    database: "hospitalmanagementsystem"
});

app.use(express.json());
app.use(cors());

app.get('/', (req, res) => {
    res.json("This is the Backend");
});



app.get('/pharmacy', (req, res) => {
    const q = "SELECT * from pharmacy";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        else return res.json(data);
    });
});

app.get('/doctors', (req, res) => {
    const q = "SELECT * from doctor";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        else return res.json(data);
    });
});

app.get('/ward', (req, res) => {
    const q = "SELECT * from ward where occupied = 0";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        else return res.json(data);
    });
});

app.get('/fetchrecord', (req, res) => {
    const q = "SELECT * from paymentrecord";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        else return res.json(data);
    });
});

app.get('/docDB', (req, res) => {
    const q = "SELECT * from employee";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        else return res.json(data);
    });
});

app.get('/doctorForDoctorPage', (req, res) => {
    const q = "SELECT docName FROM doctor WHERE employeeID = ?";
    const values = [
        req.query.doctorID
    ];
    db.query(q, values, (err, data) => {
        if (err) return res.json("Failed to Retrieve Doctor Name");
        else return res.json(data[0].docName);
    });
});

app.get('/appointments', (req, res) => {
    const { date } = req.query;
    const q = "SELECT * FROM appointment WHERE appDate = ?";
    db.query(q, [date], (err, data) => {
        if (err) return res.json(err);
        return res.json(data);
    });
});

app.get('/appointmentList', (req, res) => {
    const q = "SELECT * FROM appointment";
    db.query(q, (err, data) => {
        if (err) return res.json(err);
        return res.json(data);
    });
});

app.get('/appointmentListForDoctor', (req, res) => {
    const q = "SELECT * FROM appointment where doctorID = ?";
    const values = [
        req.query.docID
    ]
    db.query(q, values, (err, data) => {
        if (err) return res.json(err);
        return res.json(data);
    });
});

app.get('/fetchWardsOfDoctor', (req, res) => {
    const q = "SELECT wardID FROM appointment where doctorID = ?";
    const values = [
        req.query.docID
    ]
    db.query(q, values, (err, data) => {
        if (err) return res.json(err);
        return res.json(data);
    });
});

app.get('/latestPaymentID', (req, res) => {
    const qLatestPaymentID = "SELECT MAX(paymentID) AS paymentID FROM paymentrecord";
    db.query(qLatestPaymentID, (err, data) => {
        if (err) return res.json(err);
        return res.json(data);
    });
});


app.post('/addEmployee', (req, res) => {
    const qEmployee = "INSERT INTO employee (name, age, employeeID, salary, email, designation) VALUES (?, ?, ?, ?, ?, ?)";
    const valuesEmployee = [
        req.body.name,
        req.body.age,
        req.body.employeeID,
        req.body.salary,
        req.body.email,
        req.body.designation
    ];

    db.query(qEmployee, valuesEmployee, (err, data) => {
        if (err) return res.json(err);
        if (req.body.designation.toLowerCase() === 'doctor') {
            const qDoctor = "INSERT INTO doctor (specialisation, employeeID, docName, salary) VALUES (?, ?, ?, ?)";
            const valuesDoctor = [
                req.body.specialisation,
                req.body.employeeID,
                req.body.name,
                req.body.salary
            ];
            db.query(qDoctor, valuesDoctor, (err, data) => {
                if (err) return res.json(err);
                return res.json("Employee and Doctor added successfully");
            });
        } else {
            return res.json("Employee added successfully");
        }
    });
});

app.post('/deleteEmployee', (req, res) => {
    const employeeID = req.body.employeeID;

    const qFetchWards = "SELECT wardID FROM appointment WHERE doctorID = ?";

    const qDeleteDoctor = "DELETE FROM doctor WHERE employeeID = ?";
    const qDeleteEmployee = "DELETE FROM employee WHERE employeeID = ?";
    const qDeleteAppointments = "DELETE FROM appointment WHERE doctorID = ?";
    const qUpdateWards = "UPDATE ward SET occupied = 0 WHERE wardID IN (?)";

    db.query(qFetchWards, [employeeID], (err, results) => {
        if (err) return res.json(err);

        const wardIDs = results.map(row => row.wardID);

        db.beginTransaction((err) => {
            if (err) return res.json(err);

            db.query(qDeleteDoctor, [employeeID], (err, data) => {
                if (err) {
                    return db.rollback(() => {
                        res.json(err);
                    });
                }

                db.query(qDeleteEmployee, [employeeID], (err, data) => {
                    if (err) {
                        return db.rollback(() => {
                            res.json(err);
                        });
                    }

                    db.query(qDeleteAppointments, [employeeID], (err, data) => {
                        if (err) {
                            return db.rollback(() => {
                                res.json(err);
                            });
                        }

                        db.query(qUpdateWards, [wardIDs], (err, data) => {
                            if (err) {
                                return db.rollback(() => {
                                    res.json(err);
                                });
                            }

                            db.commit((err) => {
                                if (err) {
                                    return db.rollback(() => {
                                        res.json(err);
                                    });
                                }

                                res.json("Employee, doctor, appointments, and wards updated successfully");
                            });
                        });
                    });
                });
            });
        });
    });
});


app.post('/bookAppointment', (req, res) => {
    const { doctorID, patientID, appDate, wardID, slot } = req.body;
    const appIDQuery = "SELECT IFNULL(MAX(appID), 0) + 1 AS nextAppID FROM appointment";
    
    db.query(appIDQuery, (err, result) => {
        if (err) return res.json({ success: false, message: "Failed to get next appID" });

        const appID = result[0].nextAppID;
        const bookAppointmentQuery = `
            INSERT INTO appointment (appID, doctorID, patientID, appDate, wardID, slot) 
            VALUES (?, ?, ?, ?, ?, ?)
        `;

        db.query(bookAppointmentQuery, [appID, doctorID, patientID, appDate, wardID, slot], (err, data) => {
            if (err) {
                console.log(err);
                return res.json({ success: false, message: "Failed to book appointment" });
            } else {
                console.log("Appointment booked successfully");
                return res.json({ success: true, message: "Appointment booked successfully" });
            }
        });
    });
});

app.post('/deleteAppointment', (req, res) => {
    const { appointmentID, wardID } = req.body;
    const deleteAppointmentQuery = "DELETE FROM appointment WHERE appID = ?";

    db.query(deleteAppointmentQuery, [appointmentID], (err, data) => {
        if (err) {
            console.log(err);
            return res.json({ success: false, message: "Failed to delete appointment" });
        } else {
            console.log("Appointment deleted successfully");

            const unbookWardQuery = "UPDATE ward SET occupied = 0 WHERE wardID = ?";
            db.query(unbookWardQuery, [wardID], (err, data) => {
                if (err) {
                    console.log(err);
                    return res.json({ success: false, message: "Failed to unoccupy ward" });
                } else {
                    console.log("Ward unoccupied successfully");
                    return res.json({ success: true, message: "Appointment and ward updated successfully" });
                }
            });
        }
    });
});

app.post('/doctors', (req, res) => {
    const q = "INSERT INTO feedback (feedbackNo, patientID, employeeName, review) VALUES (?, ?, ?, ?)";
    const values = [
        req.body.feedbackNo,
        req.body.patientID,
        req.body.employeeName,
        req.body.review
    ];
    db.query(q, values, (err, data) => {
        if (err) return res.json("Failed to Submit Feedback");
        if (data.length > 0) {
            console.log("Feedback submitted successfully");
        }
    });
});

app.post('/paymentrecord', (req, res) => {
    const q = "INSERT INTO paymentrecord (paymentID, amount) VALUES (?, ?)";
    const values = [
        req.body.paymentID,
        req.body.amount
    ];
    db.query(q, values, (err, data) => {
        if (err) return res.json(err);
        if (data.length > 0) {
            console.log("Payment submitted successfully");
        }
    });
});

app.post('/login', (req, res) => {
    const q = "SELECT * FROM logincredentials WHERE userID = ? AND password = ?";
    const values = [
        req.body.username,
        req.body.password
    ];
    db.query(q, values, (err, data) => {
        if (err) return res.json("Login Failed");
        if (data.length > 0) {
            return res.json(data);
        } else {
            return res.json({ success: false, message: "Invalid credentials" });
        }
    });
});

app.post('/signup', (req, res) => {
    const checkUserQuery = "SELECT * FROM logincredentials WHERE userID = ?";
    const insertUserQuery = "INSERT INTO logincredentials (userID, password) VALUES (?, ?)";
    const values = [
        req.body.username,
        req.body.password
    ];
    db.query(checkUserQuery, [req.body.username], (err, data) => {
        if (err) return res.json("Sign Up Failed");
        if (data.length > 0) {
            return res.json({ success: false, message: "This user already exists" });
        } else {
            db.query(insertUserQuery, values, (err, data) => {
                if (err) return res.json("Sign Up Failed");
                return res.json({ success: true, message: "User registered successfully" });
            });
        }
    });
});

app.post('/bookWard', (req, res) => {
    const wardToBook = req.body.wardToBook;
    const bookWardQuery = "UPDATE ward SET occupied = 1 WHERE wardID = ?";

    db.query(bookWardQuery, [wardToBook], (err, data) => {
        if (err) {
            console.log(err);
            return res.json({ success: false, message: "Failed to occupy ward" });
        } else {
            console.log("Ward occupied successfully");
            return res.json({ success: true, message: "Ward occupied successfully" });
        }
    });
});

app.post('/unbookWard', (req, res) => {
    const wardToUnbook = req.body.wardToUnbook;
    const unbookWardQuery = "UPDATE ward SET occupied = 0 WHERE wardID = ?";

    db.query(unbookWardQuery, [wardToUnbook], (err, data) => {
        if (err) {
            console.log(err);
            return res.json({ success: false, message: "Failed to unoccupy ward" });
        } else {
            console.log("Ward unoccupied successfully");
            return res.json({ success: true, message: "Ward unoccupied successfully" });
        }
    });
});

app.listen(8800, () => {
    console.log("Connected to Backend!");
});
