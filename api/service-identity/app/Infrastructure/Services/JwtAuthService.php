<?php

namespace App\Infrastructure\Services;

use App\Application\Contracts\JwtServiceInterface;
use App\Domain\User\User as DomainUser;
use App\Infrastructure\Persistence\Eloquent\UserModel;
use PHPOpenSourceSaver\JWTAuth\Facades\JWTAuth;

class JwtAuthService implements JwtServiceInterface
{
    public function generateFromUser(DomainUser $user): string
    {
        $eloquentUser = UserModel::where('email', (string) $user->getEmail())->firstOrFail();

        return JWTAuth::fromUser($eloquentUser);
    }
}
