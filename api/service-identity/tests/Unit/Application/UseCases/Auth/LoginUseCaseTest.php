<?php

namespace Tests\Unit\Application\UseCases\Auth;

use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\Auth\RefreshToken;
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

        $dummyUser = $this->createDummyUser();

        $userRepoMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $jwtMock->shouldReceive('generateFromUser')
            ->once()
            ->with($dummyUser)
            ->andReturn('fake.jwt.token');

        $refreshTokenRepoMock->shouldReceive('save')
            ->once()
            ->with(Mockery::type(RefreshToken::class));

        $useCase = new LoginUseCase($userRepoMock, $jwtMock, $refreshTokenRepoMock);
        $dto = new LoginDTO('jonas@example.com', 'StrongPassword123!');

        $result = $useCase->execute($dto);

        $this->assertInstanceOf(AuthTokenDTO::class, $result);
        $this->assertEquals('fake.jwt.token', $result->accessToken);
        $this->assertNotEmpty($result->refreshToken);
    }

    public function test_it_should_throw_exception_if_user_not_found(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('wrong@example.com')
            ->andReturnNull();

        $useCase = new LoginUseCase($repositoryMock, $jwtMock, $refreshTokenRepoMock);
        $dto = new LoginDTO('wrong@example.com', 'AnyPassword123!');

        $this->expectException(InvalidCredentialsException::class);
        $this->expectExceptionMessage('identity.auth.errors.invalid_credentials');

        $useCase->execute($dto);
    }

    public function test_it_should_throw_exception_if_password_is_incorrect(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);
        $refreshTokenRepoMock = Mockery::mock(RefreshTokenRepositoryInterface::class);

        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $useCase = new LoginUseCase($repositoryMock, $jwtMock, $refreshTokenRepoMock);
        $dto = new LoginDTO('jonas@example.com', 'WrongPassword999!');

        $this->expectException(InvalidCredentialsException::class);
        $this->expectExceptionMessage('identity.auth.errors.invalid_credentials');

        $useCase->execute($dto);
    }
}
