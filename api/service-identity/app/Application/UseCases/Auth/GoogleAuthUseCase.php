<?php

namespace App\Application\UseCases\Auth;

use App\Application\DTOs\Auth\GoogleAuthDTO;
use App\Domain\User\Exceptions\InvalidBirthDateException;
use App\Domain\User\Exceptions\InvalidUserNameException;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Domain\User\User;
use App\Domain\User\ValueObjects\Email;
use App\Domain\User\ValueObjects\Password;
use App\Domain\User\ValueObjects\UserName;
use DateTimeImmutable;
use Illuminate\Support\Str;

class GoogleAuthUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    /**
     * @throws InvalidUserNameException
     * @throws InvalidBirthDateException
     */
    public function execute(GoogleAuthDTO $dto): string
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user) {
            $randomPassword = Str::random(16) . 'A!1a';
            $defaultBirthDate = new DateTimeImmutable('1970-01-01');

            $user = new User(
                name: new UserName($dto->name),
                email: new Email($dto->email),
                password: new Password($randomPassword),
                birthDate: $defaultBirthDate
            );

            $this->userRepository->save($user);
        }

        return $this->jwtService->generateFromUser($user);
    }
}
