<?php

namespace App\Domain\User\ValueObjects;

use App\Domain\User\Exceptions\InvalidPasswordException;

class Password
{
    private string $hash;

    public function __construct(string $password, bool $isAlreadyHashed = false)
    {
        if ($isAlreadyHashed) {
            $this->hash = $password;
        } else {
            if (strlen($password) < 8) {
                throw new InvalidPasswordException();
            }
            $this->hash = password_hash($password, PASSWORD_BCRYPT);
        }
    }

    public function verify(string $plainPassword): bool
    {
        return password_verify($plainPassword, $this->hash);
    }

    public function getHash(): string
    {
        return $this->hash;
    }
}
