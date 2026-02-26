<?php

namespace Tests\Unit\Application\UseCases\Auth;

use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use PHPUnit\Framework\TestCase;
use App\Application\UseCases\Auth\GoogleAuthUseCase;
use App\Application\DTOs\Auth\GoogleAuthDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Domain\User\User;
use DateTimeImmutable;
use Mockery;

class GoogleAuthUseCaseTest extends TestCase
{
    protected function tearDown(): void
    {
        Mockery::close();
        parent::tearDown();
    }

    public function test_it_should_authenticate_existing_user_without_creating_a_new_one(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $dummyUser = new User(
            new UserName('Jonas Sousa'),
            new Email('jonas@example.com'),
            new Password('Pass1234!'),
            new DateTimeImmutable('1990-01-01')
        );

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $repositoryMock->shouldNotReceive('save');

        $jwtMock->shouldReceive('generateFromUser')
            ->once()
            ->with($dummyUser)
            ->andReturn('jwt.token.existing');

        $useCase = new GoogleAuthUseCase($repositoryMock, $jwtMock);
        $dto = new GoogleAuthDTO('Jonas Sousa', 'jonas@example.com', 'google_id_123');

        $token = $useCase->execute($dto);

        $this->assertEquals('jwt.token.existing', $token);
    }

    public function test_it_should_create_new_user_if_email_does_not_exist(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $jwtMock = Mockery::mock(JwtServiceInterface::class);

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('new@example.com')
            ->andReturnNull();

        $repositoryMock->shouldReceive('save')
            ->once()
            ->with(Mockery::type(User::class));

        $jwtMock->shouldReceive('generateFromUser')
            ->once()
            ->with(Mockery::type(User::class))
            ->andReturn('jwt.token.new');

        $useCase = new GoogleAuthUseCase($repositoryMock, $jwtMock);
        $dto = new GoogleAuthDTO('New User', 'new@example.com', 'google_id_456');

        $token = $useCase->execute($dto);

        $this->assertEquals('jwt.token.new', $token);
    }
}
