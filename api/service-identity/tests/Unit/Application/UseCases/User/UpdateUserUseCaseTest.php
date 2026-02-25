<?php

namespace Tests\Unit\Application\UseCases\User;

use PHPUnit\Framework\TestCase;
use App\Application\UseCases\User\UpdateUserUseCase;
use App\Application\DTOs\User\UpdateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Exceptions\InvalidCurrentPasswordException;
use App\Domain\User\User;
use DateTimeImmutable;
use Mockery;

class UpdateUserUseCaseTest extends TestCase
{
    protected function tearDown(): void
    {
        Mockery::close();
        parent::tearDown();
    }

    private function createDummyUser(): User
    {
        return new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: new DateTimeImmutable('1990-01-01')
        );
    }

    public function test_it_can_update_user_name(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $repositoryMock->shouldReceive('save')
            ->once()
            ->with(Mockery::on(function (User $user) {
                return $user->getName() === 'Jonas Atualizado';
            }));

        $useCase = new UpdateUserUseCase($repositoryMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: 'Jonas Atualizado',
            currentPassword: null,
            newPassword: null
        );

        $useCase->execute($dto);

        $this->assertTrue(true);
    }

    public function test_it_can_update_password_if_current_password_is_correct(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $repositoryMock->shouldReceive('save')
            ->once()
            ->with(Mockery::on(function (User $user) {
                return $user->verifyPassword('NewStrongPassword456!');
            }));

        $useCase = new UpdateUserUseCase($repositoryMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: null,
            currentPassword: 'StrongPassword123!',
            newPassword: 'NewStrongPassword456!'
        );

        $useCase->execute($dto);
        $this->assertTrue(true);
    }

    public function test_it_throws_exception_if_current_password_is_incorrect_when_updating_password(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $repositoryMock->shouldNotReceive('save');

        $useCase = new UpdateUserUseCase($repositoryMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: null,
            currentPassword: 'WrongPassword999!',
            newPassword: 'NewStrongPassword456!'
        );

        $this->expectException(InvalidCurrentPasswordException::class);
        $this->expectExceptionMessage('identity.user.errors.invalid_current_password');

        $useCase->execute($dto);
    }
}
