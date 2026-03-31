# 提交变更说明（累计）

## 说明
本文件记录本轮“选择框/线框/高亮/传输管道可见性”相关的累计改动，包含之前已完成更新和本次追加更新。
不包含仓库中与该主题无关的其他功能改动。

## 主要功能更新
1. 选择框从单一 AABB 改为模型线框体系
- 新增并统一使用模型线框渲染入口，支持按子箱体渲染、颜色控制、线宽控制。
- Obsidian TNT 子箱体可高亮，支持按准星命中子箱体绘制。

2. JSON 模型解析选择框（普通方块）
- 通过方块状态 + 模型解析生成线框，不改变碰撞箱，仅改变选择框表现。
- 支持 blockstates variants 的模型定位和旋转应用。
- 针对 `mekanism:nothing`、`mekanism:basic_cube`、`mekanism:machine` 等模型支持跳过。

3. 自定义线框渲染细化
- 增加外轮廓/内部可见边控制，降低拼合交界线残留。
- 增加内部可见边白名单逻辑（按 modid）。
- 线宽可配置。
- 支持自动变色（彩虹循环）配置。

4. 与 IBlastingItem 高亮逻辑兼容
- 手持 `instanceof IBlastingItem` 时可复用模型线框高亮。
- 预览高亮与实际挖掘范围对齐，避免只显示单方块的问题。

5. 特殊渲染方块接入接口化线框
- 增加 TileEntity 侧接口驱动：`ISpecialSelectionWireframeTile`。
- 支持模型类、字段过滤、朝向/旋转变换、动画状态同步等能力。
- `SpecialSelectionWireframeRegistry` 负责按接口动态构建/缓存 provider。

6. 特殊渲染机器接入与修正
- 已接入多类普通/发电/多方块机器的模型线框。
- 修正了部分机器朝向错误、局部部件旋转未应用、局部缺失线框等问题。
- 对地震振动器/风机叶片等动态模型做状态同步渲染。
- 太阳中子活化器中间激光柱线框按需求不渲染（保留 `laserBeamToggle` 控制）。

7. 多方块技术方块（Bounding）处理
- 命中 `BlockBounding` 时，转到主方块位置获取线框，避免从属方块显示错误。

8. 大型机器弹出方向修复
- 修复大型太阳能中子活化器在特定朝向下部分面不弹出的问题。
- 修复大型化学灌注机弹出面仅在正面左侧生效的问题。

9. 传输器（transmitter）内部渲染可见性优化
- 新增共享可见性判断 `shouldRenderTransmitterInterior(...)`。
- 第一人称下按视角/FOV 与遮挡判断决定是否渲染内部内容。
- 透明方块（如玻璃、非完整立方体、液体、半透明）允许穿透继续检测，不再一刀切遮挡。
- 新增距离剔除：玩家不在附近时跳过内部渲染。

## 新增/调整配置（ClientConfig）
- `JsonSelectionBoxModIdWhitelist`：控制哪些 `modid` 启用 JSON 模型选择框解析（支持 `*`）。
- `JsonSelectionBoxModelSkipList`：指定跳过解析的模型 ID（用于排除通用壳模型或空模型）。
- `JsonSelectionBoxKeepVisibleInternalEdgesModIdWhitelist`：指定哪些 `modid` 保留内部可见边（减少过滤导致的内部线缺失）。
- `JsonSelectionBoxLineWidth`：控制模型线框粗细。
- `JsonSelectionBoxAutoColorCycle`：控制线框是否自动循环变色。
- `transmitterInteriorRenderDistance`：控制管道/线缆内部渲染距离（默认 64，`0` 表示不限制）。

## 关键改动文件
- `src/main/java/mekanism/client/render/MekanismRenderSelection.java`
- `src/main/java/mekanism/client/render/RenderTickHandler.java`
- `src/main/java/mekanism/client/render/SelectionWireframeRenderer.java`
- `src/main/java/mekanism/client/render/JsonModelSelectionBoxCache.java`
- `src/main/java/mekanism/client/render/SpecialSelectionWireframeRegistry.java`
- `src/main/java/mekanism/client/render/ISpecialSelectionWireframeProvider.java`
- `src/main/java/mekanism/common/base/ISpecialSelectionWireframeTile.java`
- `src/main/java/mekanism/common/config/ClientConfig.java`
- `src/main/java/mekanism/common/block/BlockObsidianTNT.java`
- `src/main/java/mekanism/common/block/BlockBounding.java`
- `src/main/java/mekanism/common/tile/TileEntityBoundingBlock.java`
- `src/main/java/mekanism/client/render/transmitter/RenderTransmitterBase.java`
- `src/main/java/mekanism/client/render/transmitter/RenderUniversalCable.java`
- `src/main/java/mekanism/client/render/transmitter/RenderPressurizedTube.java`
- `src/main/java/mekanism/client/render/transmitter/RenderThermodynamicConductor.java`
- `src/main/java/mekanism/client/render/transmitter/RenderMechanicalPipe.java`
- `src/main/java/mekanism/client/render/transmitter/RenderLogisticalTransporter.java`

## 已接入接口的主要 TileEntity（节选）
- `TileEntityEnergyCube`
- `TileEntityDigitalMiner`
- `TileEntitySecurityDesk`
- `TileEntitySeismicVibrator`
- `TileEntitySolarNeutronActivator`
- `TileEntityIsotopicCentrifuge`
- `TileEntityChemicalCrystallizer`
- `TileEntityAntiprotonicNucleosynthesizer`
- `TileEntityChemicalDissolutionChamber`
- `TileEntityNutritionalLiquifier`
- `TileEntityRotaryCondensentrator`
- `TileEntityQuantumEntangloporter`
- `TileEntityResistiveHeater`
- `TileEntityLargeSolarNeutronActivator`
- `TileEntityLargeChemicalInfuser`
- `TileEntityLargeElectrolyticSeparator`
- `TileEntityLargeChemicalWasher`
- `TileEntityLargeWindGenerator`
- `TileEntityLargeGasGenerator`
- `TileEntityWindGenerator`
- `TileEntitySolarGenerator`
- `TileEntityHeatGenerator`
- `TileEntityGasGenerator`
- `TileEntityBioGenerator`

## 编译验证
- 执行：`./gradlew.bat compileJava -x test --no-daemon`
- 结果：`BUILD SUCCESSFUL`

---

## 追加：本轮累计更新（2026-03-31）

### 范围说明
本节为在既有提交说明基础上的追加内容，覆盖本轮累计修复：`newgui` 交互、动态储罐单介质逻辑、动态储罐 GUI 气体容器处理、渲染一致性、大型发电机性能细节、HybridStorage 槽位映射修正等。

### 主要修复与优化
1. `newgui` 模块界面交互稳定性
- 修复 `mouseClicked` 事件重复调用导致的“单次点击触发两次”问题。
- 修复模块拖动条在鼠标释放后仍跟踪的问题（释放事件向子元素传递）。
- 增加 `newgui` 鼠标滚轮分发能力（按前后层级分发）。
- 修复窗口尺寸变化/全屏切换后 GUI 元素重建叠加问题（重建前清理旧 widgets）。

2. 动态储罐（Dynamic Tank）单介质约束
- 参考高版本行为，动态储罐改为“同一时刻仅允许一种介质（流体或气体）”。
- 在存储层、输入能力层、结构同步层、缓存层加入互斥保护与 sanitize 收敛，避免流体与气体并存状态。
- 动态阀门输入/输出能力按互斥规则限制：有气体时禁流体输入，有流体时禁气体输入。

3. 动态储罐 GUI 支持气体容器
- 容器与服务端库存处理增加 `IGasItem` 路径。
- 支持 `BOTH/FILL/EMPTY` 三种模式下气体容器的灌入/抽出。
- 处理后容器正确转移到输出槽，输入槽正确扣减。
- Shift-Click 快速移动支持识别气体容器。

4. 动态储罐渲染与显示统一
- GUI 介质显示由双显示改为单显示（按当前介质自动展示）。
- 动态储罐渲染支持：
  - 流体；
  - 有流体映射的气体；
  - 无流体映射的气体（白贴图/热贴图着色路径）。
- 按本次要求，气体无流体映射路径中相关模型贴图统一为 `heatIcon`。

5. 动态储罐网络与接口细节修正
- 客户端缩放计算增加容量为 0 的保护，避免除零窗口。
- 流体容器处理分支增加“状态变化检测”，仅在介质/输入槽/输出槽实际变化时发包，减少无效更新包。
- 计算机接口 `getCapacity` 修正为返回真实 mB 容量（`volume * FLUID_PER_TANK`），不再仅返回体积块数。

6. 动态阀门比较器逻辑修正
- 比较器输出由“仅基于流体”改为“流体/气体二者取有效存量”，单介质模式下气体存储也能正确输出红石强度。

7. HybridStorage 槽位映射修正
- 修复 `<=120` / `==128` 等边界与错位判断。
- 统一 `120~127` 功能槽位语义：气体输入/输出、流体容器输入/输出、电力充放电槽位判定与提取条件对应容器定义。
- 修复 gas tank2 的气体容器校验映射。

8. 大型发电机性能细节
- 大型风机与大型燃气机在 `addTileSyncTask` 中增加“有能量再 `CableUtils.emit`”条件，避免空能量状态下每 tick 无意义弹出。

### 本轮新增/重点涉及文件（节选）
- `src/main/java/mekanism/client/newgui/GuiMekanism.java`
- `src/main/java/mekanism/client/newgui/element/custom/GuiModuleScreen.java`
- `src/main/java/mekanism/client/gui/GuiDynamicTank.java`
- `src/main/java/mekanism/client/render/tileentity/RenderDynamicTank.java`
- `src/main/java/mekanism/common/content/tank/SynchronizedTankData.java`
- `src/main/java/mekanism/common/content/tank/DynamicFluidTank.java`
- `src/main/java/mekanism/common/content/tank/DynamicGasTank.java`
- `src/main/java/mekanism/common/content/tank/TankUpdateProtocol.java`
- `src/main/java/mekanism/common/content/tank/TankCache.java`
- `src/main/java/mekanism/common/inventory/container/ContainerDynamicTank.java`
- `src/main/java/mekanism/common/tile/multiblock/TileEntityDynamicTank.java`
- `src/main/java/mekanism/common/tile/multiblock/TileEntityDynamicValve.java`
- `src/main/java/mekanism/common/tile/TileEntityHybridStorage.java`
- `src/main/java/mekanism/multiblockmachine/common/tile/generator/TileEntityLargeWindGenerator.java`
- `src/main/java/mekanism/multiblockmachine/common/tile/generator/TileEntityLargeGasGenerator.java`

### 编译验证
- 执行：`./gradlew.bat compileJava -x test`
- 结果：`BUILD SUCCESSFUL`

---

## 追加：前序已完成修复归档（会话内历史）

### 说明
本节用于补充“本轮之前已完成的修复”，避免提交说明只覆盖最新一次改动。

### 历史修复（已完成）
1. `GuiModuleTweaker` / `newgui` 交互链路
- 修复界面尺寸变化后元素重复叠加问题（重建前清理旧控件）。
- 修复模块界面滑条在鼠标松开后仍跟踪的问题（释放事件下发到子元素）。
- 增加 `newgui` 鼠标滚轮分发处理。

2. 动态储罐读档/进服状态一致性
- 针对玩家进世界后动态储罐介质概率丢失问题，补齐结构缓存、协议同步与状态收敛链路。
- 在结构数据对象、缓存、合并协议中加入状态清理与一致性处理，降低结构重建/同步时的异常状态。

3. 动态储罐介质模型升级（参考高版本行为）
- 从“可同时存流体+气体”改为“同一时刻仅一种介质”。
- 动态阀门的流体/气体输入输出能力按互斥规则限制。
- 服务端 tick、客户端收包、缓存加载/同步阶段均执行互斥收敛。

4. 动态储罐 GUI 与容器能力扩展
- `ContainerDynamicTank` 支持将 `IGasItem` 作为可快速转移对象。
- 动态储罐库存处理新增气体容器路径，支持 `BOTH/FILL/EMPTY`。
- 动态阀门物品槽合法性校验支持流体容器和气体容器。

5. 动态储罐渲染与界面统一
- `GuiDynamicTank` 调整为单介质显示（根据当前存储自动显示流体或气体）。
- `RenderDynamicTank` 支持气体有流体映射与无流体映射两种渲染路径。
- 对无流体映射气体使用统一贴图+着色策略（后续本轮已进一步统一为 `heatIcon`）。

### 历史阶段编译验证
- `./gradlew.bat compileJava -x test` 已通过（历史轮次与本轮均通过）。
