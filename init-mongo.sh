#!/bin/bash
mongosh mongodb://localhost:27017 -u root -p root <<EOF
use mongo-db

var deviceIds = [];
db.device.insert([
    {
        "_id": ObjectId(),
        "name": "Security Camera",
        "description": "Motion-detection camera",
        "type": "Camera",
        "attributes": [
            {
                "attributeType": "Resolution",
                "attributeValue": "4K"
            },
            {
                "attributeType": "Night Vision",
                "attributeValue": "Yes"
            }
        ]
    },
    {
        "_id": ObjectId(),
        "name": "Door Protect Device",
        "description": "Smart door protector",
        "type": "DoorProtect",
        "attributes": [
            {
                "attributeType": "Lock Type",
                "attributeValue": "Electronic"
            },
            {
                "attributeType": "Access Method",
                "attributeValue": "Keyless Entry"
            }
        ]
    },
    {
        "_id": ObjectId(),
        "name": "Leaks Protect Device",
        "description": "Water leak detector",
        "type": "LeaksProtect",
        "attributes": [
            {
                "attributeType": "Alert Threshold",
                "attributeValue": "Low"
            },
            {
                "attributeType": "Sensor Type",
                "attributeValue": "Moisture"
            }
        ]
    },
    {
        "_id": ObjectId(),
        "name": "Smart Socket",
        "description": "WiFi-enabled smart socket",
        "type": "SmartSocket",
        "attributes": [
            {
                "attributeType": "Control Type",
                "attributeValue": "Remote"
            },
            {
                "attributeType": "Power Consumption",
                "attributeValue": "10W"
            }
        ]
    }
]);

db.device.find({}, { _id: 1 }).forEach(function(device) {
    deviceIds.push(device._id);
});

function getRandomDeviceId() {
    return deviceIds[Math.floor(Math.random() * deviceIds.length)];
}

db.user.insert([
    {
        "username": "HansSchmidt",
        "email": "hans.schmidt@example.com",
        "mobileNumber": "+491234567890",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "OWNER"
            },
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
        "username": "MariaMuller",
        "email": "maria.muller@example.com",
        "mobileNumber": "+49876543210",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "AnnaSchneider",
        "email": "anna.schneider@example.com",
        "mobileNumber": "+491234567891",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "MaxWagner",
        "email": "max.wagner@example.com",
        "mobileNumber": "+49876543211",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
        "username": "SophieBecker",
        "email": "sophie.becker@example.com",
        "mobileNumber": "+491234567892",
        "password": "hashed_password",
        "devices": []
    },
    {
        "username": "LukasSchulz",
        "email": "lukas.schulz@example.com",
        "mobileNumber": "+49876543212",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "ElenaKoch",
        "email": "elena.koch@example.com",
        "mobileNumber": "+491234567893",
        "password": "hashed_password",
        "devices": [
            {
                "deviceId": getRandomDeviceId(),
                "userDeviceId": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
            "username": "User1",
            "email": "user1@example.com",
            "mobileNumber": "+1111111111",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User2",
            "email": "user2@example.com",
            "mobileNumber": "+2222222222",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User3",
            "email": "user3@example.com",
            "mobileNumber": "+3333333333",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User4",
            "email": "user4@example.com",
            "mobileNumber": "+4444444444",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User5",
            "email": "user5@example.com",
            "mobileNumber": "+5555555555",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User6",
            "email": "user6@example.com",
            "mobileNumber": "+6666666666",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User7",
            "email": "user7@example.com",
            "mobileNumber": "+7777777777",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User8",
            "email": "user8@example.com",
            "mobileNumber": "+8888888888",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User9",
            "email": "user9@example.com",
            "mobileNumber": "+9999999999",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User10",
            "email": "user10@example.com",
            "mobileNumber": "+1010101010",
            "password": "hashed_password",
            "devices": [
                {
                    "deviceId": getRandomDeviceId(),
                    "userDeviceId": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        }
]);
EOF
