<?php

namespace App\Application\Contracts;

use App\Domain\User\User;

interface JwtServiceInterface
{
    public function generateFromUser(User $user): string;
}
