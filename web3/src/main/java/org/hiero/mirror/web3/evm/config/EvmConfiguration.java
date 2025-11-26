// SPDX-License-Identifier: Apache-2.0

package org.hiero.mirror.web3.evm.config;

import static org.hyperledger.besu.evm.internal.EvmConfiguration.WorldUpdaterMode.JOURNALED;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.hedera.hapi.node.base.SemanticVersion;
import com.hedera.node.app.service.evm.contracts.execution.traceability.MPCQEvmOperationTracer;
import com.hedera.node.app.service.evm.contracts.operations.CreateOperationExternalizer;
import com.hedera.node.app.service.evm.contracts.operations.MPCQBalanceOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQBalanceOperationV038;
import com.hedera.node.app.service.evm.contracts.operations.MPCQDelegateCallOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQEvmChainIdOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQEvmCreate2Operation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQEvmCreateOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQEvmSLoadOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQExtCodeCopyOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQExtCodeHashOperation;
import com.hedera.node.app.service.evm.contracts.operations.MPCQExtCodeHashOperationV038;
import com.hedera.node.app.service.evm.contracts.operations.MPCQExtCodeSizeOperation;
import com.hedera.services.contracts.gascalculator.GasCalculatorMPCQV22;
import com.hedera.services.evm.contracts.operations.MPCQPrngSeedOperation;
import com.hedera.services.evm.contracts.operations.MPCQSelfDestructOperation;
import com.hedera.services.evm.contracts.operations.MPCQSelfDestructOperationV038;
import com.hedera.services.evm.contracts.operations.MPCQSelfDestructOperationV046;
import com.hedera.services.evm.contracts.operations.MPCQSelfDestructOperationV050;
import com.hedera.services.txns.crypto.AbstractAutoCreationLogic;
import com.hedera.services.txns.util.PrngLogic;
import jakarta.inject.Provider;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.hiero.mirror.web3.evm.contracts.execution.MirrorEvmMessageCallProcessor;
import org.hiero.mirror.web3.evm.contracts.execution.MirrorEvmMessageCallProcessorV30;
import org.hiero.mirror.web3.evm.contracts.execution.MirrorEvmMessageCallProcessorV50;
import org.hiero.mirror.web3.evm.contracts.execution.traceability.MirrorOperationTracer;
import org.hiero.mirror.web3.evm.contracts.execution.traceability.OpcodeTracer;
import org.hiero.mirror.web3.evm.contracts.execution.traceability.TracerType;
import org.hiero.mirror.web3.evm.contracts.operations.MPCQBlockHashOperation;
import org.hiero.mirror.web3.evm.contracts.operations.MPCQCustomCallOperation;
import org.hiero.mirror.web3.evm.properties.MirrorNodeEvmProperties;
import org.hiero.mirror.web3.evm.store.contract.EntityAddressSequencer;
import org.hiero.mirror.web3.repository.properties.CacheProperties;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.EvmSpecVersion;
import org.hyperledger.besu.evm.MainnetEVMs;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.gascalculator.GasCalculator;
import org.hyperledger.besu.evm.operation.BalanceOperation;
import org.hyperledger.besu.evm.operation.ExtCodeHashOperation;
import org.hyperledger.besu.evm.operation.OperationRegistry;
import org.hyperledger.besu.evm.operation.SelfDestructOperation;
import org.hyperledger.besu.evm.precompile.KZGPointEvalPrecompiledContract;
import org.hyperledger.besu.evm.precompile.PrecompileContractRegistry;
import org.hyperledger.besu.evm.processor.ContractCreationProcessor;
import org.hyperledger.besu.evm.processor.MessageCallProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class EvmConfiguration {

    public static final String CACHE_MANAGER_CONTRACT = "contract";
    public static final String CACHE_MANAGER_CONTRACT_SLOTS = "contractSlots";
    public static final String CACHE_MANAGER_CONTRACT_STATE = "contractState";
    public static final String CACHE_MANAGER_ENTITY = "entity";
    public static final String CACHE_MANAGER_RECORD_FILE_LATEST = "recordFileLatest";
    public static final String CACHE_MANAGER_RECORD_FILE_EARLIEST = "recordFileEarliest";
    public static final String CACHE_MANAGER_RECORD_FILE_INDEX = "recordFileIndex";
    public static final String CACHE_MANAGER_RECORD_FILE_TIMESTAMP = "recordFileTimestamp";
    public static final String CACHE_MANAGER_SLOTS_PER_CONTRACT = "slotsPerContract";
    public static final String CACHE_MANAGER_SYSTEM_FILE = "systemFile";
    public static final String CACHE_MANAGER_SYSTEM_FILE_MODULARIZED = "systemFileModularized";
    public static final String CACHE_MANAGER_SYSTEM_ACCOUNT = "systemAccount";
    public static final String CACHE_MANAGER_TOKEN = "token";
    public static final String CACHE_MANAGER_TOKEN_TYPE = "tokenType";
    public static final String CACHE_NAME = "default";
    public static final String CACHE_NAME_CONTRACT = "contract";
    public static final String CACHE_NAME_EVM_ADDRESS = "evmAddress";
    public static final String CACHE_NAME_ALIAS = "alias";
    public static final String CACHE_NAME_EXCHANGE_RATE = "exchangeRate";
    public static final String CACHE_NAME_FEE_SCHEDULE = "feeSchedule";
    public static final String CACHE_NAME_MODULARIZED = "cacheModularized";
    public static final String CACHE_NAME_NFT = "nft";
    public static final String CACHE_NAME_NFT_ALLOWANCE = "nftAllowance";
    public static final String CACHE_NAME_RECORD_FILE_LATEST = "latest";
    public static final String CACHE_NAME_RECORD_FILE_LATEST_INDEX = "latestIndex";
    public static final String CACHE_NAME_TOKEN = "token";
    public static final String CACHE_NAME_TOKEN_ACCOUNT = "tokenAccount";
    public static final String CACHE_NAME_TOKEN_ACCOUNT_COUNT = "tokenAccountCount";
    public static final String CACHE_NAME_TOKEN_ALLOWANCE = "tokenAllowance";
    public static final String CACHE_NAME_TOKEN_AIRDROP = "tokenAirdrop";
    public static final SemanticVersion EVM_VERSION_0_30 = new SemanticVersion(0, 30, 0, "", "");
    public static final SemanticVersion EVM_VERSION_0_34 = new SemanticVersion(0, 34, 0, "", "");
    public static final SemanticVersion EVM_VERSION_0_38 = new SemanticVersion(0, 38, 0, "", "");
    public static final SemanticVersion EVM_VERSION_0_46 = new SemanticVersion(0, 46, 0, "", "");
    public static final SemanticVersion EVM_VERSION_0_50 = new SemanticVersion(0, 50, 0, "", "");
    public static final SemanticVersion EVM_VERSION_0_51 = new SemanticVersion(0, 51, 0, "", "");
    public static final SemanticVersion EVM_VERSION = EVM_VERSION_0_51;
    private final CacheProperties cacheProperties;
    private final MirrorNodeEvmProperties mirrorNodeEvmProperties;
    private final GasCalculatorMPCQV22 gasCalculator;
    private final MPCQBlockHashOperation hederaBlockHashOperation;
    private final MPCQExtCodeHashOperation hederaExtCodeHashOperation;
    private final MPCQExtCodeHashOperationV038 hederaExtCodeHashOperationV038;
    private final AbstractAutoCreationLogic autoCreationLogic;
    private final EntityAddressSequencer entityAddressSequencer;
    private final PrecompiledContractProvider precompilesHolder;
    private final BiPredicate<Address, MessageFrame> addressValidator;
    private final Predicate<Address> systemAccountDetector;

    @Bean(CACHE_MANAGER_CONTRACT)
    CacheManager cacheManagerContract() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME_CONTRACT));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getContract());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_CONTRACT_SLOTS)
    CacheManager cacheManagerContractSlots() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Set.of(CACHE_NAME));
        cacheManager.setCacheSpecification(cacheProperties.getContractSlots());
        return cacheManager;
    }

    @Bean(CACHE_MANAGER_CONTRACT_STATE)
    CacheManager cacheManagerContractState() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getContractState());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_SYSTEM_ACCOUNT)
    CacheManager cacheManagerSystemAccount() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getSystemAccount());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_ENTITY)
    CacheManager cacheManagerEntity() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME, CACHE_NAME_EVM_ADDRESS, CACHE_NAME_ALIAS));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getEntity());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_SLOTS_PER_CONTRACT)
    CaffeineCacheManager cacheManagerSlotsPerContract() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheSpecification(cacheProperties.getSlotsPerContract());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_TOKEN)
    CacheManager cacheManagerToken() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(
                CACHE_NAME_NFT,
                CACHE_NAME_NFT_ALLOWANCE,
                CACHE_NAME_TOKEN,
                CACHE_NAME_TOKEN_ACCOUNT,
                CACHE_NAME_TOKEN_ACCOUNT_COUNT,
                CACHE_NAME_TOKEN_ALLOWANCE,
                CACHE_NAME_TOKEN_AIRDROP));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getToken());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_TOKEN_TYPE)
    CacheManager cacheManagerTokenType() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getTokenType());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_SYSTEM_FILE)
    CacheManager cacheManagerSystemFile() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME_EXCHANGE_RATE, CACHE_NAME_FEE_SCHEDULE));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getFee());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_SYSTEM_FILE_MODULARIZED)
    CacheManager cacheManagerSystemFileModularized() {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME_MODULARIZED));
        caffeineCacheManager.setCacheSpecification(cacheProperties.getFee());
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_RECORD_FILE_INDEX)
    @Primary
    CacheManager cacheManagerRecordFileIndex() {
        final var caffeine = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10000)
                .recordStats();
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_RECORD_FILE_TIMESTAMP)
    CacheManager cacheManagerRecordFileTimestamp() {
        final var caffeine = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(10000)
                .recordStats();
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_RECORD_FILE_LATEST)
    CacheManager cacheManagerRecordFileLatest() {
        final var caffeine = Caffeine.newBuilder()
                .expireAfterWrite(500, TimeUnit.MILLISECONDS)
                .maximumSize(1)
                .recordStats();
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME_RECORD_FILE_LATEST, CACHE_NAME_RECORD_FILE_LATEST_INDEX));
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean(CACHE_MANAGER_RECORD_FILE_EARLIEST)
    CacheManager cacheManagerRecordFileEarliest() {
        final var caffeine = Caffeine.newBuilder().maximumSize(1).recordStats();
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCacheNames(Set.of(CACHE_NAME));
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    Map<TracerType, Provider<MPCQEvmOperationTracer>> monoTracerProvider(
            final MirrorOperationTracer mirrorOperationTracer, final OpcodeTracer opcodeTracer) {
        Map<TracerType, Provider<MPCQEvmOperationTracer>> tracerMap = new EnumMap<>(TracerType.class);
        tracerMap.put(TracerType.OPCODE, () -> opcodeTracer);
        tracerMap.put(TracerType.OPERATION, () -> mirrorOperationTracer);
        return tracerMap;
    }

    @Bean
    Map<SemanticVersion, Provider<ContractCreationProcessor>> contractCreationProcessorProvider(
            final ContractCreationProcessor contractCreationProcessor30,
            final ContractCreationProcessor contractCreationProcessor34,
            final ContractCreationProcessor contractCreationProcessor38,
            final ContractCreationProcessor contractCreationProcessor46,
            final ContractCreationProcessor contractCreationProcessor50) {
        Map<SemanticVersion, Provider<ContractCreationProcessor>> processorsMap = new HashMap<>();
        processorsMap.put(EVM_VERSION_0_30, () -> contractCreationProcessor30);
        processorsMap.put(EVM_VERSION_0_34, () -> contractCreationProcessor34);
        processorsMap.put(EVM_VERSION_0_38, () -> contractCreationProcessor38);
        processorsMap.put(EVM_VERSION_0_46, () -> contractCreationProcessor46);
        processorsMap.put(EVM_VERSION_0_50, () -> contractCreationProcessor50);
        processorsMap.put(EVM_VERSION_0_51, () -> contractCreationProcessor50);
        return processorsMap;
    }

    @Bean
    Map<SemanticVersion, Provider<MessageCallProcessor>> messageCallProcessors(
            MirrorEvmMessageCallProcessorV30 mirrorEvmMessageCallProcessor30,
            MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor34,
            MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor38,
            MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor46,
            MirrorEvmMessageCallProcessorV50 mirrorEvmMessageCallProcessor50) {
        Map<SemanticVersion, Provider<MessageCallProcessor>> processorsMap = new HashMap<>();
        processorsMap.put(EVM_VERSION_0_30, () -> mirrorEvmMessageCallProcessor30);
        processorsMap.put(EVM_VERSION_0_34, () -> mirrorEvmMessageCallProcessor34);
        processorsMap.put(EVM_VERSION_0_38, () -> mirrorEvmMessageCallProcessor38);
        processorsMap.put(EVM_VERSION_0_46, () -> mirrorEvmMessageCallProcessor46);
        processorsMap.put(EVM_VERSION_0_50, () -> mirrorEvmMessageCallProcessor50);
        processorsMap.put(EVM_VERSION_0_51, () -> mirrorEvmMessageCallProcessor50);
        return processorsMap;
    }

    @Bean
    CreateOperationExternalizer createOperationExternalizer() {
        return new CreateOperationExternalizer() {
            @Override
            public void externalize(final MessageFrame frame, final MessageFrame childFrame) {
                // do nothing
            }

            @Override
            public boolean shouldFailBasedOnLazyCreation(final MessageFrame frame, final Address contractAddress) {
                return false;
            }
        };
    }

    @Bean
    org.hyperledger.besu.evm.internal.EvmConfiguration provideEvmConfiguration() {
        return new org.hyperledger.besu.evm.internal.EvmConfiguration(
                org.hyperledger.besu.evm.internal.EvmConfiguration.DEFAULT.jumpDestCacheWeightKB(), JOURNALED);
    }

    @Bean
    EVM evm030(
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQSelfDestructOperation hederaSelfDestructOperation,
            final MPCQBalanceOperation hederaBalanceOperation) {
        return evm(
                gasCalculator,
                mirrorNodeEvmProperties,
                prngSeedOperation,
                hederaBlockHashOperation,
                hederaExtCodeHashOperation,
                hederaSelfDestructOperation,
                hederaBalanceOperation,
                EvmSpecVersion.LONDON,
                MainnetEVMs::registerLondonOperations);
    }

    @Bean
    EVM evm034(
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQSelfDestructOperation hederaSelfDestructOperation,
            final MPCQBalanceOperation hederaBalanceOperation) {
        return evm(
                gasCalculator,
                mirrorNodeEvmProperties,
                prngSeedOperation,
                hederaBlockHashOperation,
                hederaExtCodeHashOperation,
                hederaSelfDestructOperation,
                hederaBalanceOperation,
                EvmSpecVersion.PARIS,
                MainnetEVMs::registerParisOperations);
    }

    @Bean
    EVM evm038(
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQSelfDestructOperationV038 hederaSelfDestructOperationV038,
            final MPCQBalanceOperationV038 hederaBalanceOperationV038) {
        return evm(
                gasCalculator,
                mirrorNodeEvmProperties,
                prngSeedOperation,
                hederaBlockHashOperation,
                hederaExtCodeHashOperationV038,
                hederaSelfDestructOperationV038,
                hederaBalanceOperationV038,
                EvmSpecVersion.SHANGHAI,
                MainnetEVMs::registerShanghaiOperations);
    }

    @Bean
    EVM evm046(
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQSelfDestructOperationV046 hederaSelfDestructOperationV046,
            final MPCQBalanceOperationV038 hederaBalanceOperationV038) {
        return evm(
                gasCalculator,
                mirrorNodeEvmProperties,
                prngSeedOperation,
                hederaBlockHashOperation,
                hederaExtCodeHashOperationV038,
                hederaSelfDestructOperationV046,
                hederaBalanceOperationV038,
                EvmSpecVersion.SHANGHAI,
                MainnetEVMs::registerShanghaiOperations);
    }

    @Bean
    EVM evm050(
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQSelfDestructOperationV050 hederaSelfDestructOperationV050,
            final MPCQBalanceOperationV038 hederaBalanceOperationV038) {
        KZGPointEvalPrecompiledContract.init();
        return evm(
                gasCalculator,
                mirrorNodeEvmProperties,
                prngSeedOperation,
                hederaBlockHashOperation,
                hederaExtCodeHashOperationV038,
                hederaSelfDestructOperationV050,
                hederaBalanceOperationV038,
                EvmSpecVersion.CANCUN,
                MainnetEVMs::registerCancunOperations);
    }

    @Bean
    MPCQPrngSeedOperation hederaPrngSeedOperation(final GasCalculator gasCalculator, final PrngLogic prngLogic) {
        return new MPCQPrngSeedOperation(gasCalculator, prngLogic);
    }

    @Bean
    MPCQSelfDestructOperation hederaSelfDestructOperation(final GasCalculator gasCalculator) {
        return new MPCQSelfDestructOperation(gasCalculator, addressValidator);
    }

    @Bean
    MPCQSelfDestructOperationV038 hederaSelfDestructOperationV038(final GasCalculator gasCalculator) {
        return new MPCQSelfDestructOperationV038(gasCalculator, addressValidator, systemAccountDetector);
    }

    @Bean
    MPCQBalanceOperation hederaBalanceOperation(final GasCalculator gasCalculator) {
        return new MPCQBalanceOperation(gasCalculator, addressValidator);
    }

    @Bean
    MPCQBalanceOperationV038 hederaBalanceOperationV038(final GasCalculator gasCalculator) {
        return new MPCQBalanceOperationV038(
                gasCalculator, addressValidator, systemAccountDetector, mirrorNodeEvmProperties);
    }

    @Bean
    MPCQSelfDestructOperationV046 hederaSelfDestructOperationV046(final GasCalculator gasCalculator) {
        return new MPCQSelfDestructOperationV046(gasCalculator, addressValidator, systemAccountDetector, false);
    }

    @Bean
    MPCQSelfDestructOperationV050 hederaSelfDestructOperationV050(final GasCalculator gasCalculator) {
        return new MPCQSelfDestructOperationV050(gasCalculator, addressValidator, systemAccountDetector);
    }

    @Bean
    PrecompileContractRegistry precompileContractRegistry() {
        return new PrecompileContractRegistry();
    }

    @Bean
    public ContractCreationProcessor contractCreationProcessor30(@Qualifier("evm030") EVM evm) {
        return contractCreationProcessor(evm);
    }

    @Bean
    public ContractCreationProcessor contractCreationProcessor34(@Qualifier("evm034") EVM evm) {
        return contractCreationProcessor(evm);
    }

    @Bean
    public ContractCreationProcessor contractCreationProcessor38(@Qualifier("evm038") EVM evm) {
        return contractCreationProcessor(evm);
    }

    @Bean
    public ContractCreationProcessor contractCreationProcessor46(@Qualifier("evm046") EVM evm) {
        return contractCreationProcessor(evm);
    }

    @Bean
    public ContractCreationProcessor contractCreationProcessor50(@Qualifier("evm050") EVM evm) {
        return contractCreationProcessor(evm);
    }

    @Bean
    public MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor34(@Qualifier("evm034") EVM evm) {
        return mirrorEvmMessageCallProcessor(evm);
    }

    @Bean
    public MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor38(@Qualifier("evm038") EVM evm) {
        return mirrorEvmMessageCallProcessor(evm);
    }

    @Bean
    public MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor46(@Qualifier("evm046") EVM evm) {
        return mirrorEvmMessageCallProcessor(evm);
    }

    @SuppressWarnings("java:S107")
    private EVM evm(
            final GasCalculator gasCalculator,
            final MirrorNodeEvmProperties mirrorNodeEvmProperties,
            final MPCQPrngSeedOperation prngSeedOperation,
            final MPCQBlockHashOperation hederaBlockHashOperation,
            final ExtCodeHashOperation extCodeHashOperation,
            final SelfDestructOperation selfDestructOperation,
            final BalanceOperation hederaBalanceOperation,
            EvmSpecVersion specVersion,
            OperationRegistryCallback callback) {
        final var operationRegistry = new OperationRegistry();
        final BiPredicate<Address, MessageFrame> validator = (Address x, MessageFrame y) -> true;

        callback.register(
                operationRegistry,
                gasCalculator,
                mirrorNodeEvmProperties.chainIdBytes32().toBigInteger());
        Set.of(
                        new MPCQDelegateCallOperation(gasCalculator, validator),
                        new MPCQEvmChainIdOperation(gasCalculator, mirrorNodeEvmProperties),
                        new MPCQEvmCreate2Operation(
                                gasCalculator, mirrorNodeEvmProperties, createOperationExternalizer()),
                        new MPCQEvmCreateOperation(gasCalculator, createOperationExternalizer()),
                        new MPCQEvmSLoadOperation(gasCalculator),
                        new MPCQExtCodeCopyOperation(gasCalculator, validator),
                        new MPCQExtCodeSizeOperation(gasCalculator, validator),
                        new MPCQCustomCallOperation(gasCalculator),
                        prngSeedOperation,
                        hederaBlockHashOperation,
                        extCodeHashOperation,
                        selfDestructOperation,
                        hederaBalanceOperation)
                .forEach(operationRegistry::put);

        return new EVM(operationRegistry, gasCalculator, provideEvmConfiguration(), specVersion);
    }

    private ContractCreationProcessor contractCreationProcessor(EVM evm) {
        return new ContractCreationProcessor(evm, true, List.of(), 1);
    }

    private MirrorEvmMessageCallProcessor mirrorEvmMessageCallProcessor(EVM evm) {
        return new MirrorEvmMessageCallProcessor(
                autoCreationLogic,
                entityAddressSequencer,
                evm,
                precompileContractRegistry(),
                precompilesHolder,
                gasCalculator,
                systemAccountDetector);
    }
}
