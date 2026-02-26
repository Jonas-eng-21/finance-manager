<?php

namespace App\Application\UseCases\User;

use App\Application\DTOs\User\CreateUserDTO;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use App\Domain\User\Exceptions\InvalidUserNameException;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\EmailAlreadyExistsException;
use App\Domain\User\User;
use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use DateTimeImmutable;

class CreateUserUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    /**
     * @throws InvalidUserNameException
     * @throws EmailAlreadyExistsException
     * @throws InvalidBirthDateException
     * @throws \Exception
     */
    public function execute(CreateUserDTO $dto): string
    {
        if ($this->userRepository->existsByEmail($dto->email)) {
            throw new EmailAlreadyExistsException();
        }

        $user = new User(
            name: new UserName($dto->name),
            email: new Email($dto->email),
            password: new Password($dto->password),
            birthDate: new \DateTimeImmutable($dto->birthDate)
        );

        $this->userRepository->save($user);

        return $this->jwtService->generateFromUser($user);
    }
}
