<?php

namespace App\Domain\User\ValueObjects;

use App\Domain\User\Exceptions\InvalidUserNameException;

readonly class UserName
{
    private string $value;

    public function __construct(string $name)
    {
        $normalized = trim($name);

        if ($normalized === '' || strlen($normalized) < 3 || strlen($normalized) > 120) {
            throw new InvalidUserNameException();
        }

        $this->value = $normalized;
    }

    public function getValue(): string
    {
        return $this->value;
    }

    public function __toString(): string
    {
        return $this->value;
    }
}
