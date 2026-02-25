<?php

namespace App\Application\DTOs\User;

readonly class UpdateUserDTO
{
    public function __construct(
        public string $email,
        public ?string $name = null,
        public ?string $currentPassword = null,
        public ?string $newPassword = null
    ) {}
}
