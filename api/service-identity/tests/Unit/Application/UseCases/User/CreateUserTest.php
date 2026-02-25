<?php

namespace Tests\Unit\Application\UseCases\User;

use PHPUnit\Framework\TestCase;
use App\Application\UseCases\User\CreateUserUseCase;
use App\Application\DTOs\User\CreateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\EmailAlreadyExistsException;
use Mockery;

class CreateUserTest extends TestCase
{
    protected function tearDown(): void
    {
        Mockery::close();
        parent::tearDown();
    }

    public function test_it_should_not_allow_duplicate_email(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $repositoryMock->shouldReceive('existsByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn(true);

        $useCase = new CreateUserUseCase($repositoryMock, $jwtMock);

        $dto = new CreateUserDTO(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: '1990-01-01'
        );

        $this->expectException(EmailAlreadyExistsException::class);
        $this->expectExceptionMessage('identity.user.errors.email_already_exists');

        $useCase->execute($dto);
    }

    public function test_it_should_create_user_and_return_jwt(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $repositoryMock->shouldReceive('existsByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn(false);

        $repositoryMock->shouldReceive('save')
            ->once()
            ->andReturnUsing(function ($user) {
                $this->assertEquals('jonas@example.com', (string) $user->getEmail());
            });

        $jwtMock->shouldReceive('generateFromUser')
            ->once()
            ->andReturn('fake.jwt.token');

        $useCase = new CreateUserUseCase($repositoryMock, $jwtMock);

        $dto = new CreateUserDTO(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: '1990-01-01'
        );

        $token = $useCase->execute($dto);

        $this->assertEquals('fake.jwt.token', $token);
    }
}
