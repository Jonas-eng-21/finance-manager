<?php

namespace Tests\Unit\Application\UseCases\Auth;

use PHPUnit\Framework\TestCase;
use App\Application\UseCases\Auth\LoginUseCase;
use App\Application\DTOs\Auth\LoginDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\InvalidCredentialsException;
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
        return new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: new DateTimeImmutable('1990-01-01')
        );
    }

    public function test_it_should_return_jwt_on_valid_credentials(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $jwtMock->shouldReceive('generateFromUser')
            ->once()
            ->with($dummyUser)
            ->andReturn('fake.jwt.token');

        $useCase = new LoginUseCase($repositoryMock, $jwtMock);
        $dto = new LoginDTO('jonas@example.com', 'StrongPassword123!');

        $token = $useCase->execute($dto);

        $this->assertEquals('fake.jwt.token', $token);
    }

    public function test_it_should_throw_exception_if_user_not_found(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('wrong@example.com')
            ->andReturnNull();

        $useCase = new LoginUseCase($repositoryMock, $jwtMock);
        $dto = new LoginDTO('wrong@example.com', 'AnyPassword123!');

        $this->expectException(InvalidCredentialsException::class);
        $this->expectExceptionMessage('identity.auth.errors.invalid_credentials');

        $useCase->execute($dto);
    }

    public function test_it_should_throw_exception_if_password_is_incorrect(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $dummyUser = $this->createDummyUser();

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $useCase = new LoginUseCase($repositoryMock, $jwtMock);
        $dto = new LoginDTO('jonas@example.com', 'WrongPassword999!');

        $this->expectException(InvalidCredentialsException::class);
        $this->expectExceptionMessage('identity.auth.errors.invalid_credentials');

        $useCase->execute($dto);
    }
}
