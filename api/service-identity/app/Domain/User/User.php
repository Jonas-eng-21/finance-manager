<?php

namespace App\Domain\User;

use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use DateTimeImmutable;

class User
{
    private string $name;
    private Email $email;
    private Password $password;
    private DateTimeImmutable $birthDate;

    public function __construct(
        string $name,
        string $email,
        string $password,
        DateTimeImmutable $birthDate
    ) {
        if ($birthDate > new DateTimeImmutable()) {
            throw new InvalidBirthDateException();
        }

        $this->name = $name;
        $this->email = new Email($email);
        $this->password = new Password($password);
        $this->birthDate = $birthDate;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function getEmail(): Email
    {
        return $this->email;
    }

    public function verifyPassword(string $plainPassword): bool
    {
        return $this->password->verify($plainPassword);
    }

    public function getPasswordHash(): string
    {
        return $this->password->getHash();
    }

    public function getBirthDate(): DateTimeImmutable
    {
        return $this->birthDate;
    }
}
