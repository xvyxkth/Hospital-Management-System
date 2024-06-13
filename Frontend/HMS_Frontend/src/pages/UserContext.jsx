import React, { createContext, useState } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [userString, setUserString] = useState("");

    return (
        <UserContext.Provider value={{ userString, setUserString }}>
            {children}
        </UserContext.Provider>
    );
};

export default UserContext;