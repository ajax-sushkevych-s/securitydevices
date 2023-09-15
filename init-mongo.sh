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
                "attribute_type": "Resolution",
                "attribute_value": "4K"
            },
            {
                "attribute_type": "Night Vision",
                "attribute_value": "Yes"
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
                "attribute_type": "Lock Type",
                "attribute_value": "Electronic"
            },
            {
                "attribute_type": "Access Method",
                "attribute_value": "Keyless Entry"
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
                "attribute_type": "Alert Threshold",
                "attribute_value": "Low"
            },
            {
                "attribute_type": "Sensor Type",
                "attribute_value": "Moisture"
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
                "attribute_type": "Control Type",
                "attribute_value": "Remote"
            },
            {
                "attribute_type": "Power Consumption",
                "attribute_value": "10W"
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
        "mobile_number": "+491234567890",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "OWNER"
            },
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
        "username": "MariaMuller",
        "email": "maria.muller@example.com",
        "mobile_number": "+49876543210",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "AnnaSchneider",
        "email": "anna.schneider@example.com",
        "mobile_number": "+491234567891",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "MaxWagner",
        "email": "max.wagner@example.com",
        "mobile_number": "+49876543211",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
        "username": "SophieBecker",
        "email": "sophie.becker@example.com",
        "mobile_number": "+491234567892",
        "password": "hashed_password",
        "devices": []
    },
    {
        "username": "LukasSchulz",
        "email": "lukas.schulz@example.com",
        "mobile_number": "+49876543212",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "OWNER"
            }
        ]
    },
    {
        "username": "ElenaKoch",
        "email": "elena.koch@example.com",
        "mobile_number": "+491234567893",
        "password": "hashed_password",
        "devices": [
            {
                "device_id": getRandomDeviceId(),
                "user_device_id": ObjectId(),
                "role": "VIEWER"
            }
        ]
    },
    {
            "username": "User1",
            "email": "user1@example.com",
            "mobile_number": "+1111111111",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User2",
            "email": "user2@example.com",
            "mobile_number": "+2222222222",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User3",
            "email": "user3@example.com",
            "mobile_number": "+3333333333",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User4",
            "email": "user4@example.com",
            "mobile_number": "+4444444444",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User5",
            "email": "user5@example.com",
            "mobile_number": "+5555555555",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User6",
            "email": "user6@example.com",
            "mobile_number": "+6666666666",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User7",
            "email": "user7@example.com",
            "mobile_number": "+7777777777",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User8",
            "email": "user8@example.com",
            "mobile_number": "+8888888888",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        },
        {
            "username": "User9",
            "email": "user9@example.com",
            "mobile_number": "+9999999999",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "OWNER"
                }
            ]
        },
        {
            "username": "User10",
            "email": "user10@example.com",
            "mobile_number": "+1010101010",
            "password": "hashed_password",
            "devices": [
                {
                    "device_id": getRandomDeviceId(),
                    "user_device_id": ObjectId(),
                    "role": "VIEWER"
                }
            ]
        }
]);
EOF
