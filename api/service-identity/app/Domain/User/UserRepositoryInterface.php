<?php

namespace App\Domain\User;

interface UserRepositoryInterface
{
    public function existsByEmail(string $email): bool;

    public function save(User $user): void;

    public function findByEmail(string $email): ?User;

    public function delete(string $email): void;

    public function findById(int $id): ?User;
}
