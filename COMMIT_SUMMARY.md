# 提交摘要（精简）

## 本次改动
- 选择框渲染从单一 AABB 扩展为模型线框（JSON 模型 + 特殊渲染 TileEntity 接口）。
- 统一了模型线框绘制入口，支持子箱体、线宽、自动变色、内部边控制。
- 修复多类机器的朝向/旋转/局部缺线问题，并接入动态模型状态同步。
- 命中 `BlockBounding` 时改为解析主方块线框。
- 手持 `IBlastingItem` 时，高亮预览与实际挖掘范围对齐。
- 优化 transmitter 内部渲染：
  - 第一人称按视角和遮挡决定是否渲染；
  - 透明方块不再完全遮挡内部渲染；
  - 玩家距离过远时跳过内部渲染。

## 配置新增/调整（ClientConfig）
- `JsonSelectionBoxModIdWhitelist`
- `JsonSelectionBoxModelSkipList`
- `JsonSelectionBoxKeepVisibleInternalEdgesModIdWhitelist`
- `JsonSelectionBoxLineWidth`
- `JsonSelectionBoxAutoColorCycle`
- `transmitterInteriorRenderDistance`

## 验证
- `./gradlew.bat compileJava -x test --no-daemon` 通过（BUILD SUCCESSFUL）。

---

## 追加：本轮提交摘要（2026-03-31）

### 核心改动（精简）
- 修复 `newgui` 点击重复触发、拖条释放后仍跟踪、窗口重建叠加、滚轮分发缺失。
- 动态储罐改为单介质（流体/气体互斥），并补齐协议/缓存/输入能力层保护。
- 动态储罐 GUI 支持气体容器（含 Shift-Click、BOTH/FILL/EMPTY 三模式）。
- 动态储罐渲染同步升级；气体无流体映射路径贴图统一为 `heatIcon`。
- 修复动态阀门比较器仅识别流体的问题，气体存储也可正确输出红石强度。
- 修复 HybridStorage 功能槽位边界与映射错误（`120~127`）。
- 大型风机/大型燃气机加“有能量才 emit”判断，降低空转开销。
- 动态储罐无变化发包优化 + 计算机接口容量返回值修正（mB）。

### 验证
- `./gradlew.bat compileJava -x test` 通过（BUILD SUCCESSFUL）。

---

## 追加：前序修复摘要（会话内历史）

### 精简补充
- 已在更早轮次完成 `GuiModuleTweaker/newgui` 的 resize、拖条释放、滚轮交互修复。
- 已完成动态储罐进服同步稳定性修复（缓存/协议/状态收敛链路）。
- 已完成动态储罐单介质改造（流体/气体互斥）及阀门能力互斥限制。
- 已完成动态储罐 GUI/容器的气体容器支持（含 Shift-Click 与模式处理）。
- 已完成动态储罐 GUI/渲染单介质统一改造。

### 验证
- `./gradlew.bat compileJava -x test` 通过。
