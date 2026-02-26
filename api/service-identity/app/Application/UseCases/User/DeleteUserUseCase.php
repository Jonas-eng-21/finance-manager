<?php

namespace App\Application\UseCases\User;

use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\EventDispatcherInterface;
use App\Domain\User\Events\UserDeletedEvent;
use Exception;

class DeleteUserUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly EventDispatcherInterface $eventDispatcher
    ) {}

    public function execute(string $email): void
    {
        $user = $this->userRepository->findByEmail($email);

        if (!$user) {
            throw new Exception("User not found.");
        }

        $this->userRepository->delete($email);

        $this->eventDispatcher->dispatch(new UserDeletedEvent($email));
    }
}
