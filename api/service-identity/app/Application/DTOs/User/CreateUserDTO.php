<?php

namespace App\Application\DTOs\User;

readonly class CreateUserDTO
{
    public function __construct(
        public string $name,
        public string $email,
        public string $password,
        public string $birthDate
    ) {}
}
