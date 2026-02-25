<?php

return [
    'user' => [
        'errors' => [
            'invalid_password' => 'A senha deve conter pelo menos 8 caracteres.',
            'invalid_birth_date' => 'A data de nascimento não pode estar no futuro.',
            'invalid_email' => 'O formato de e-mail fornecido é inválido.',
            'email_already_exists' => 'O e-mail fornecido já está em uso.',
            'invalid_name' => 'O nome fornecido é inválido ou não atende aos requisitos de tamanho.',
            'same_password' => 'A nova senha não pode ser igual à senha atual.',
            'invalid_current_password' => 'A senha atual fornecida está incorreta.',
        ],
    ],
    'auth' => [
        'errors' => [
            'invalid_credentials' => 'E-mail ou senha inválidos.',
        ],
    ],
];
