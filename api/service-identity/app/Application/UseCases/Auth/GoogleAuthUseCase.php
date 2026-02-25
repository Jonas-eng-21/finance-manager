<?php

namespace App\Application\UseCases\Auth;

use App\Application\DTOs\Auth\GoogleAuthDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Domain\User\User;
use DateTimeImmutable;
use Illuminate\Support\Str;

class GoogleAuthUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    public function execute(GoogleAuthDTO $dto): string
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user) {
            $randomPassword = Str::random(16) . 'A!1a';
            $defaultBirthDate = new DateTimeImmutable('1970-01-01');

            $user = new User(
                name: $dto->name,
                email: $dto->email,
                password: $randomPassword,
                birthDate: $defaultBirthDate
            );

            $this->userRepository->save($user);
        }

        return $this->jwtService->generateFromUser($user);
    }
}
