<?php

namespace App\Application\DTOs\Auth;

readonly class AuthTokenDTO
{
    public function __construct(
        public string $accessToken,
        public string $refreshToken
    ) {}
}
