<?php

namespace App\Http\Controllers;

use App\Application\DTOs\Auth\LoginDTO;
use App\Application\Exceptions\InvalidRefreshTokenException;
use App\Application\UseCases\Auth\LoginUseCase;
use App\Application\Exceptions\InvalidCredentialsException;
use App\Application\UseCases\Auth\LogoutUseCase;
use App\Application\UseCases\Auth\RefreshTokenUseCase;
use App\Http\Requests\LoginRequest;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Routing\Controller;
use Laravel\Socialite\Facades\Socialite;
use App\Application\DTOs\Auth\GoogleAuthDTO;
use App\Application\UseCases\Auth\GoogleAuthUseCase;

class AuthController extends Controller
{
    public function login(LoginRequest $request, LoginUseCase $useCase): JsonResponse
    {
        try {
            $dto = new LoginDTO(
                email: $request->validated('email'),
                password: $request->validated('password')
            );

            $authTokenDTO = $useCase->execute($dto);

            return response()->json([
                'access_token' => $authTokenDTO->accessToken,
                'refresh_token' => $authTokenDTO->refreshToken,
                'token_type' => 'Bearer',
                'expires_in' => env('JWT_TTL', 15) * 60
            ], 200);

        } catch (InvalidCredentialsException $e) {
            return response()->json([
                'error' => __($e->getMessage())
            ], 401);
        }
    }

    public function me(): JsonResponse
    {
        return response()->json(auth('api')->user(), 200);
    }

    public function redirectToGoogle()
    {
        return Socialite::driver('google')->stateless()->redirect();
    }

    public function handleGoogleCallback(GoogleAuthUseCase $useCase): JsonResponse
    {
        try {
            $googleUser = Socialite::driver('google')->stateless()->user();

            $dto = new GoogleAuthDTO(
                name: $googleUser->getName(),
                email: $googleUser->getEmail(),
                providerId: $googleUser->getId()
            );

            $token = $useCase->execute($dto);

            return response()->json(['token' => $token], 200);

        } catch (\Exception $e) {
            return response()->json(['error' => 'Google Authentication Failed'], 401);
        }
    }

    public function refresh(Request $request, RefreshTokenUseCase $useCase): JsonResponse
    {
        $request->validate(['refresh_token' => 'required|string']);

        try {
            $authTokenDTO = $useCase->execute($request->input('refresh_token'));

            return response()->json([
                'access_token' => $authTokenDTO->accessToken,
                'refresh_token' => $authTokenDTO->refreshToken,
                'token_type' => 'Bearer',
                'expires_in' => env('JWT_TTL', 15) * 60
            ], 200);

        } catch (InvalidRefreshTokenException $e) {
            return response()->json(['error' => __($e->getMessage())], 401);
        }
    }

    public function logout(Request $request, LogoutUseCase $useCase): JsonResponse
    {
        $userId = auth()->user()->id;

        $useCase->execute($userId);

        return response()->json(['message' => 'Successfully logged out']);
    }
}
