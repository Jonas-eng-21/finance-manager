<?php

namespace Tests\Unit\Application\UseCases\User;

use App\Application\Contracts\AuditLoggerInterface;
use App\Domain\Audit\Enums\AuditAction;
use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use PHPUnit\Framework\TestCase;
use App\Application\UseCases\User\UpdateUserUseCase;
use App\Application\DTOs\User\UpdateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\User\User;
use DateTimeImmutable;
use Exception;
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
        $realHash = password_hash('StrongPassword123!', PASSWORD_BCRYPT);
        return new User(
            new UserName('Jonas Sousa'),
            new Email('jonas@example.com'),
            new Password($realHash, true),
            new DateTimeImmutable('1990-01-01'),
            1
        );
    }

    public function test_it_can_update_user_name(): void
    {
        $userRepositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);

        $dummyUser = $this->createDummyUser();

        $userRepositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $userRepositoryMock->shouldReceive('save')->once();

        $auditLoggerMock->shouldReceive('log')
            ->once()
            ->with(AuditAction::PROFILE_UPDATED, 1);

        $refreshTokenRepoMock->shouldReceive('revokeAllForUser')->never();

        $useCase = new UpdateUserUseCase($userRepositoryMock, $auditLoggerMock, $refreshTokenRepoMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: 'Jonas Atualizado',
            currentPassword: null,
            newPassword: null
        );

        $useCase->execute($dto);

        $this->assertEquals('Jonas Atualizado', $dummyUser->getName());
    }

    public function test_it_can_update_password_if_current_password_is_correct(): void
    {
        $userRepositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);

        $dummyUser = $this->createDummyUser();

        $userRepositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $userRepositoryMock->shouldReceive('save')->once();

        $auditLoggerMock->shouldReceive('log')
            ->once()
            ->with(AuditAction::PASSWORD_CHANGED, 1);

        $refreshTokenRepoMock->shouldReceive('revokeAllForUser')->once()->with(1);

        $useCase = new UpdateUserUseCase($userRepositoryMock, $auditLoggerMock, $refreshTokenRepoMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: null,
            currentPassword: 'StrongPassword123!',
            newPassword: 'NewStrongPassword456!'
        );

        $useCase->execute($dto);
        $this->assertTrue($dummyUser->verifyPassword('NewStrongPassword456!'));
    }

    public function test_it_throws_exception_if_current_password_is_incorrect_when_updating_password(): void
    {
        $userRepositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);

        $dummyUser = $this->createDummyUser();

        $userRepositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $userRepositoryMock->shouldNotReceive('save');

        $auditLoggerMock->shouldReceive('log')->never();

        $refreshTokenRepoMock->shouldReceive('revokeAllForUser')->never();

        $useCase = new UpdateUserUseCase($userRepositoryMock, $auditLoggerMock, $refreshTokenRepoMock);

        $dto = new UpdateUserDTO(
            email: 'jonas@example.com',
            name: null,
            currentPassword: 'WrongPassword999!',
            newPassword: 'NewStrongPassword456!'
        );

        $this->expectException(Exception::class);
        $this->expectExceptionMessage('identity.user.errors.invalid_current_password');

        $useCase->execute($dto);
    }
}
