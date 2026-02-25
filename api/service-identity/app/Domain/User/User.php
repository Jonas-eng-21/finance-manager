<?php

namespace App\Domain\User;

use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use App\Domain\User\Exceptions\InvalidUserNameException;
use App\Domain\User\Exceptions\SamePasswordException;
use App\Application\Exceptions\InvalidCurrentPasswordException;
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

    public static function restore(string $name, string $email, string $passwordHash, DateTimeImmutable $birthDate): self
    {
        $user = (new \ReflectionClass(self::class))->newInstanceWithoutConstructor();

        $user->name = $name;
        $user->email = new Email($email);
        $user->password = new Password($passwordHash, true);
        $user->birthDate = $birthDate;

        return $user;
    }

    public function updateName(string $newName): void
    {
        $newName = trim($newName);

        if ($newName === '') {
            throw new InvalidUserNameException();
        }

        if (strlen($newName) < 3 || strlen($newName) > 120) {
            throw new InvalidUserNameException();
        }

        if ($newName === $this->name) {
            return;
        }

        $this->name = $newName;
    }


    public function updatePassword(string $currentPassword, string $newPassword): void
    {
        if (!$this->verifyPassword($currentPassword)) {
            throw new InvalidCurrentPasswordException();
        }

        if ($this->verifyPassword($newPassword)) {
            throw new SamePasswordException();
        }

        $this->password = new Password($newPassword);
    }
}
