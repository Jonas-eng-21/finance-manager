<?php

namespace App\Infrastructure\Persistence\Eloquent;

use App\Domain\User\UserRepositoryInterface;
use App\Domain\User\User as DomainUser;

class EloquentUserRepository implements UserRepositoryInterface
{
    public function existsByEmail(string $email): bool
    {
        return UserModel::where('email', $email)->exists();
    }

    public function save(DomainUser $user): void
    {
        UserModel::create([
            'name' => $user->getName(),
            'email' => (string) $user->getEmail(),
            'password' => $user->getPasswordHash(),
            'birth_date' => $user->getBirthDate()->format('Y-m-d'),
        ]);
    }
}
