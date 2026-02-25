<?php

namespace App\Application\DTOs\Auth;

readonly class GoogleAuthDTO
{
    public function __construct(
        public string $name,
        public string $email,
        public string $providerId
    ) {}
}
