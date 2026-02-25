<?php

return [
    'user' => [
        'errors' => [
            'invalid_password' => 'The password must be at least 8 characters long.',
            'invalid_birth_date' => 'The birth date cannot be in the future.',
            'invalid_email' => 'The provided email format is invalid.',
            'email_already_exists' => 'The provided email is already registered.',
            'invalid_name' => 'The provided name is invalid or does not meet the length requirements.',
            'same_password' => 'The new password cannot be the same as the current password.',
            'invalid_current_password' => 'The current password provided is incorrect.',
        ],
    ],
    'auth' => [
        'errors' => [
            'invalid_credentials' => 'Invalid email or password.',
        ],
    ],
];
