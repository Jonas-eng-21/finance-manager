<?php

namespace App\Application\UseCases\User;

use App\Application\DTOs\User\CreateUserDTO;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\EmailAlreadyExistsException;
use App\Domain\User\User;
use DateTimeImmutable;

class CreateUserUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    public function execute(CreateUserDTO $dto): string
    {
        if ($this->userRepository->existsByEmail($dto->email)) {
            throw new EmailAlreadyExistsException();
        }

        $user = new User(
            name: $dto->name,
            email: $dto->email,
            password: $dto->password,
            birthDate: new DateTimeImmutable($dto->birthDate)
        );

        $this->userRepository->save($user);

        return $this->jwtService->generateFromUser($user);
    }
}
