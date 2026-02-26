<?php

namespace Tests\Unit\Application\UseCases\User;

use PHPUnit\Framework\TestCase;
use App\Application\UseCases\User\DeleteUserUseCase;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\EventDispatcherInterface;
use App\Domain\User\Events\UserDeletedEvent;
use App\Domain\User\User;
use DateTimeImmutable;
use Exception;
use Mockery;

class DeleteUserUseCaseTest extends TestCase
{
    protected function tearDown(): void
    {
        Mockery::close();
        parent::tearDown();
    }

    public function test_it_deletes_user_and_dispatches_domain_event(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $dispatcherMock = Mockery::mock(EventDispatcherInterface::class);

        $dummyUser = new User(
            name: 'Jonas Sousa',
            email: 'jonas@example.com',
            password: 'StrongPassword123!',
            birthDate: new DateTimeImmutable('1990-01-01')
        );

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('jonas@example.com')
            ->andReturn($dummyUser);

        $repositoryMock->shouldReceive('delete')
            ->once()
            ->with('jonas@example.com');

        $dispatcherMock->shouldReceive('dispatch')
            ->once()
            ->with(Mockery::type(UserDeletedEvent::class));

        $useCase = new DeleteUserUseCase($repositoryMock, $dispatcherMock);


        $useCase->execute('jonas@example.com');

        $this->assertTrue(true);
    }

    public function test_it_throws_exception_if_user_not_found(): void
    {
        $repositoryMock = Mockery::mock(UserRepositoryInterface::class);
        $dispatcherMock = Mockery::mock(EventDispatcherInterface::class);

        $repositoryMock->shouldReceive('findByEmail')
            ->once()
            ->with('ghost@example.com')
            ->andReturnNull();

        $repositoryMock->shouldNotReceive('delete');
        $dispatcherMock->shouldNotReceive('dispatch');

        $useCase = new DeleteUserUseCase($repositoryMock, $dispatcherMock);

        $this->expectException(Exception::class);
        $this->expectExceptionMessage('User not found.');

        $useCase->execute('ghost@example.com');
    }
}
