<?php

namespace App\Domain\User;

use App\Domain\User\Exceptions\InvalidCurrentPasswordException;
use App\Domain\User\Exceptions\InvalidEmailException;
use App\Domain\User\Exceptions\InvalidPasswordException;
use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use App\Domain\User\Exceptions\InvalidUserNameException;
use App\Domain\User\Exceptions\SamePasswordException;
use App\Domain\User\ValueObjects\UserName;
use DateTimeImmutable;

class User
{
    public function __construct(
        private UserName $name,
        private Email $email,
        private Password $password,
        private \DateTimeImmutable $birthDate
    ) {
        $hoje = new \DateTimeImmutable();
        if ($birthDate > $hoje) {
            throw new InvalidBirthDateException();
        }
    }

    public function getName(): string
    {
        return $this->name->getValue();
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

    /**
     * @throws InvalidUserNameException
     * @throws InvalidEmailException
     * @throws InvalidPasswordException
     * @throws InvalidBirthDateException
     */
    public static function restore(
        string $name,
        string $email,
        string $password,
        DateTimeImmutable $birthDate
    ): self {
        return new self(
            new UserName($name),
            new Email($email),
            new Password($password, true),
            $birthDate
        );
    }

    public function updateName(UserName $newName): void
    {
        if ($newName->getValue() === $this->name->getValue()) {
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
