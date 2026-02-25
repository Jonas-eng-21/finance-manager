<?php

namespace App\Application\UseCases\Auth;

use App\Application\DTOs\Auth\LoginDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\InvalidCredentialsException;

class LoginUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    public function execute(LoginDTO $dto): string
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user) {
            throw new InvalidCredentialsException();
        }

        if (!$user->verifyPassword($dto->password)) {
            throw new InvalidCredentialsException();
        }

        return $this->jwtService->generateFromUser($user);
    }
}
