<?php

return [
    'user' => [
        'errors' => [
            'invalid_password' => 'The password must be at least 8 characters long.',
            'invalid_birth_date' => 'The birth date cannot be in the future.',
            'invalid_email' => 'The provided email format is invalid.',
            'email_already_exists' => 'The provided email is already registered.',
        ],
    ],
    'auth' => [
        'errors' => [
            'invalid_credentials' => 'Invalid email or password.',
        ],
    ],
];
