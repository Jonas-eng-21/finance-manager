<?php

namespace App\Application\UseCases\Auth;

use App\Application\DTOs\Auth\AuthTokenDTO;
use App\Application\DTOs\Auth\LoginDTO;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\Auth\RefreshToken;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\InvalidCredentialsException;
use Illuminate\Support\Str;
use DateTimeImmutable;

class LoginUseCase
{
    public function __construct(
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService,
        private readonly RefreshTokenRepositoryInterface $refreshTokenRepository
    ) {}

    public function execute(LoginDTO $dto): AuthTokenDTO
    {
        $user = $this->userRepository->findByEmail($dto->email);

        if (!$user || !$user->verifyPassword($dto->password)) {
            throw new InvalidCredentialsException();
        }

        $accessToken = $this->jwtService->generateFromUser($user);

        $plainRefreshToken = Str::random(64);

        $refreshTokenEntity = new RefreshToken(
            id: Str::uuid()->toString(),
            userId: $user->getId(),
            tokenHash: hash('sha256', $plainRefreshToken),
            expiresAt: new DateTimeImmutable('+7 days')
        );

        $this->refreshTokenRepository->save($refreshTokenEntity);

        return new AuthTokenDTO(
            accessToken: $accessToken,
            refreshToken: $plainRefreshToken
        );
    }
}
