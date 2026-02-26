<?php

namespace App\Application\UseCases\User;

use App\Application\DTOs\User\UpdateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Domain\User\ValueObjects\UserName;
use Exception;

class UpdateUserUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository
    ) {}

    public function execute(UpdateUserDTO $dto): void
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user) {
            throw new Exception("User not found.");
        }

        if ($dto->name !== null) {
            $user->updateName(new UserName($dto->name));
        }

        if ($dto->newPassword !== null) {
            $user->updatePassword($dto->currentPassword ?? '', $dto->newPassword);
        }

        $this->userRepository->save($user);
    }
}
