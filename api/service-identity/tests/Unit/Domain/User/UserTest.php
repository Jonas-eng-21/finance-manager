<?php

namespace Tests\Unit\Domain\User;

use PHPUnit\Framework\TestCase;
use App\Domain\User\User;
use App\Domain\User\Exceptions\InvalidPasswordException;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use DateTimeImmutable;

class UserTest extends TestCase
{
    public function test_it_should_create_user_with_valid_data(): void
    {
        $user = new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: new DateTimeImmutable('1990-01-01')
        );

        $this->assertEquals('Jonas Sousa', $user->getName());
        $this->assertEquals('jonas@example.com', (string) $user->getEmail());

        $this->assertTrue($user->verifyPassword('StrongPassword123!'));
    }

    public function test_it_should_fail_if_password_less_than_8_characters(): void
    {
        $this->expectException(InvalidPasswordException::class);
        $this->expectExceptionMessage('identity.user.errors.invalid_password');

        new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: '123',
            birthDate: new DateTimeImmutable('1990-01-01')
        );
    }

    public function test_it_should_fail_if_birth_date_is_in_the_future(): void
    {
        $this->expectException(InvalidBirthDateException::class);

        new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: new DateTimeImmutable('+1 day')
        );
    }
}
