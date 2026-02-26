<?php

namespace Tests\Unit\Application\UseCases\Auth;

use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\Auth\RefreshToken;
use App\Application\Contracts\AuditLoggerInterface;
use App\Domain\Audit\Enums\AuditAction;
use PHPUnit\Framework\TestCase;
use App\Application\UseCases\Auth\LoginUseCase;
use App\Application\DTOs\Auth\LoginDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\InvalidCredentialsException;
use App\Application\DTOs\Auth\AuthTokenDTO;
use App\Domain\User\User;
use DateTimeImmutable;
use Mockery;

class LoginUseCaseTest extends TestCase
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

    public function test_it_should_return_tokens_on_valid_credentials(): void
    {
        $userRepoMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);

        $dummyUser = $this->createDummyUser();

        $userRepoMock->shouldReceive('findByEmail')->once()->with('jonas@example.com')->andReturn($dummyUser);
        $jwtMock->shouldReceive('generateFromUser')->once()->with($dummyUser)->andReturn('fake.jwt.token');
        $refreshTokenRepoMock->shouldReceive('save')->once()->with(Mockery::type(RefreshToken::class));
        $auditLoggerMock->shouldReceive('log')->once()->with(AuditAction::LOGIN_SUCCESS, 1);

        $useCase = new LoginUseCase($userRepoMock, $jwtMock, $refreshTokenRepoMock, $auditLoggerMock);
        $dto = new LoginDTO('jonas@example.com', 'StrongPassword123!');

        $result = $useCase->execute($dto);

        $this->assertInstanceOf(AuthTokenDTO::class, $result);
    }

    public function test_it_should_throw_exception_if_user_not_found(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);

        $repositoryMock->shouldReceive('findByEmail')->once()->with('wrong@example.com')->andReturnNull();
        $auditLoggerMock->shouldReceive('log')->once()->with(AuditAction::LOGIN_FAILED, null);

        $useCase = new LoginUseCase($repositoryMock, $jwtMock, $refreshTokenRepoMock, $auditLoggerMock);
        $dto = new LoginDTO('wrong@example.com', 'AnyPassword123!');

        $this->expectException(InvalidCredentialsException::class);
        $useCase->execute($dto);
    }

    public function test_it_should_throw_exception_if_password_is_incorrect(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);
        $auditLoggerMock = Mockery::mock(AuditLoggerInterface::class);

        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')->once()->with('jonas@example.com')->andReturn($dummyUser);
        $auditLoggerMock->shouldReceive('log')->once()->with(AuditAction::LOGIN_FAILED, 1);

        $useCase = new LoginUseCase($repositoryMock, $jwtMock, $refreshTokenRepoMock, $auditLoggerMock);
        $dto = new LoginDTO('jonas@example.com', 'WrongPassword999!');

        $this->expectException(InvalidCredentialsException::class);
        $useCase->execute($dto);
    }
}
