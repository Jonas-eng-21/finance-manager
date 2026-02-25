<?php

return [
    'user' => [
        'errors' => [
            'invalid_password' => 'A senha deve conter pelo menos 8 caracteres.',
            'invalid_birth_date' => 'A data de nascimento não pode estar no futuro.',
            'invalid_email' => 'O formato de e-mail fornecido é inválido.',
            'email_already_exists' => 'O e-mail fornecido já está em uso.',
        ],
    ],
    'auth' => [
        'errors' => [
            'invalid_credentials' => 'E-mail ou senha inválidos.',
        ],
    ],
];
