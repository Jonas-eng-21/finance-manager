<?php

namespace App\Domain\User;

interface UserRepositoryInterface
{
    public function existsByEmail(string $email): bool;

    public function save(User $user): void;
}
