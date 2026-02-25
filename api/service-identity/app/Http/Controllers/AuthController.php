<?php

namespace App\Http\Controllers;

use App\Application\DTOs\Auth\LoginDTO;
use App\Application\UseCases\Auth\LoginUseCase;
use App\Application\Exceptions\InvalidCredentialsException;
use App\Http\Requests\LoginRequest;
use Illuminate\Http\JsonResponse;
use Illuminate\Routing\Controller;

class AuthController extends Controller
{
    public function login(LoginRequest $request, LoginUseCase $useCase): JsonResponse
    {
        $validated = $request->validated();

        $dto = new LoginDTO(
            email: $validated['email'],
            password: $validated['password']
        );

        try {
            $token = $useCase->execute($dto);

            return response()->json(['token' => $token], 200); // 200 OK para leitura/busca

        } catch (InvalidCredentialsException $e) {
            // Retorna 401 Unauthorized para credenciais inválidas
            return response()->json(['error' => __($e->getMessage())], 401);
        } catch (\Exception $e) {
            return response()->json(['error' => 'Internal Server Error'], 500);
        }
    }
}
