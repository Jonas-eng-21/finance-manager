<?php

namespace App\Application\UseCases\Auth;

use App\Application\DTOs\Auth\AuthTokenDTO;
use App\Domain\Auth\RefreshTokenRepositoryInterface;
use App\Domain\Auth\RefreshToken;
use App\Domain\User\UserRepositoryInterface;
use App\Application\Contracts\JwtServiceInterface;
use App\Application\Exceptions\InvalidRefreshTokenException;
use Illuminate\Support\Str;
use DateTimeImmutable;

class RefreshTokenUseCase
{
    public function __construct(
        private readonly RefreshTokenRepositoryInterface $refreshTokenRepository,
        private readonly UserRepositoryInterface $userRepository,
        private readonly JwtServiceInterface $jwtService
    ) {}

    public function execute(string $plainRefreshToken): AuthTokenDTO
    {
        $tokenHash = hash('sha256', $plainRefreshToken);
        $tokenEntity = $this->refreshTokenRepository->findByTokenHash($tokenHash);

        if (!$tokenEntity || !$tokenEntity->isValid()) {
            throw new InvalidRefreshTokenException();
        }

        $user = $this->userRepository->findById($tokenEntity->getUserId());
        if (!$user) {
            throw new InvalidRefreshTokenException();
        }

        $tokenEntity->revoke();
        $this->refreshTokenRepository->save($tokenEntity);

        $newAccessToken = $this->jwtService->generateFromUser($user);
        $newPlainRefreshToken = Str::random(64);

        $newRefreshTokenEntity = new RefreshToken(
            id: Str::uuid()->toString(),
            userId: $user->getId(),
            tokenHash: hash('sha256', $newPlainRefreshToken),
            expiresAt: new DateTimeImmutable('+7 days')
        );

        $this->refreshTokenRepository->save($newRefreshTokenEntity);

        return new AuthTokenDTO($newAccessToken, $newPlainRefreshToken);
    }
}
